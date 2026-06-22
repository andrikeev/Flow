package flow.network.impl

import flow.data.api.repository.SettingsRepository
import flow.dispatchers.api.Dispatchers
import flow.logger.api.LoggerFactory
import flow.network.api.ProxyController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.Authenticator
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI
import flow.models.settings.Proxy as ProxyConfig
import flow.models.settings.ProxyType

/**
 * Routes all OkHttp traffic through the user configured proxy.
 *
 * Acts as the [ProxySelector] for the shared [okhttp3.OkHttpClient] and additionally
 * provides HTTP proxy authentication via [authenticate]. SOCKS proxy authentication is
 * handled through the default [Authenticator].
 *
 * When no proxy is configured it falls back to the system default selector,
 * preserving the previous behaviour.
 *
 * @see <a href=https://github.com/square/okhttp/issues/6877#issuecomment-1438554879>workaround</>
 */
class ProxyControllerImpl(
    private val settingsRepository: SettingsRepository,
    private val dispatchers: Dispatchers,
    // Provider breaks the dependency cycle: the OkHttpClient is built using this selector.
    private val okHttpClientProvider: () -> OkHttpClient,
    loggerFactory: LoggerFactory,
) : ProxySelector(), ProxyController {
    private val logger = loggerFactory.get("ProxyController")
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.default)

    @Volatile
    private var proxy: Proxy? = null

    @Volatile
    private var credentials: Pair<String, String>? = null

    private var initialized = false

    override fun setup() {
        scope.launch {
            settingsRepository.observeSettings()
                .map { it.proxy }
                .distinctUntilChanged()
                .collect(::applyProxy)
        }
    }

    private fun applyProxy(config: ProxyConfig) {
        val isValid = config.enabled &&
            config.host.isNotBlank() &&
            config.port in 1..65535
        if (isValid) {
            val type = when (config.type) {
                ProxyType.HTTP -> Proxy.Type.HTTP
                ProxyType.SOCKS -> Proxy.Type.SOCKS
            }
            proxy = Proxy(type, InetSocketAddress.createUnresolved(config.host, config.port))
            credentials = config.username
                .takeIf(String::isNotBlank)
                ?.let { it to config.password }
            // OkHttp delegates SOCKS proxy auth to the default Authenticator.
            Authenticator.setDefault(credentials?.let(::proxyAuthenticator))
            logger.d { "Proxy enabled: ${config.type} ${config.host}:${config.port}" }
        } else {
            proxy = null
            credentials = null
            Authenticator.setDefault(null)
            logger.d { "Proxy disabled" }
        }
        // Drop pooled connections so the new configuration takes effect immediately for
        // hosts already contacted. OkHttp would otherwise keep reusing existing sockets,
        // bypassing the proxy change until the pool evicts naturally or the app restarts.
        // Skipped on the very first application to avoid building the client prematurely.
        if (initialized) {
            runCatching { okHttpClientProvider().connectionPool.evictAll() }
        }
        initialized = true
    }

    private fun proxyAuthenticator(credentials: Pair<String, String>): Authenticator {
        val (username, password) = credentials
        return object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication? {
                return if (requestorType == RequestorType.PROXY) {
                    PasswordAuthentication(username, password.toCharArray())
                } else {
                    null
                }
            }
        }
    }

    override fun select(uri: URI?): List<Proxy> {
        val proxy = proxy
        return if (proxy != null) {
            listOf(proxy)
        } else {
            runCatching { getDefault().select(uri) }.getOrElse { listOf(Proxy.NO_PROXY) }
        }
    }

    override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
        if (proxy == null) {
            getDefault().connectFailed(uri, sa, ioe)
        }
    }

    /**
     * Adds basic `Proxy-Authorization` credentials for authenticated HTTP proxies.
     * Returns `null` when there are no credentials or the request was already attempted.
     */
    fun authenticate(response: Response): Request? {
        val credentials = credentials ?: return null
        if (response.request.header(ProxyAuthorizationHeader) != null) {
            return null
        }
        val credential = Credentials.basic(credentials.first, credentials.second)
        return response.request.newBuilder()
            .header(ProxyAuthorizationHeader, credential)
            .build()
    }

    private companion object {
        const val ProxyAuthorizationHeader = "Proxy-Authorization"
    }
}
