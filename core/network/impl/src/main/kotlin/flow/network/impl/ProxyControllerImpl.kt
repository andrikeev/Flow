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
import javax.inject.Inject
import javax.inject.Singleton
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
@Singleton
class ProxyControllerImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dispatchers: Dispatchers,
    loggerFactory: LoggerFactory,
) : ProxySelector(), ProxyController {
    private val logger = loggerFactory.get("ProxyController")
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.default)

    @Volatile
    private var proxy: Proxy? = null

    @Volatile
    private var credentials: Pair<String, String>? = null

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
        if (!isValid) {
            proxy = null
            credentials = null
            Authenticator.setDefault(null)
            logger.d { "Proxy disabled" }
            return
        }
        val type = when (config.type) {
            ProxyType.HTTP -> Proxy.Type.HTTP
            ProxyType.SOCKS -> Proxy.Type.SOCKS
        }
        proxy = Proxy(type, InetSocketAddress.createUnresolved(config.host, config.port))
        credentials = config.username
            .takeIf(String::isNotBlank)
            ?.let { it to config.password }
        // OkHttp delegates SOCKS proxy auth to the default Authenticator.
        Authenticator.setDefault(
            credentials?.let { (username, password) ->
                object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication? {
                        return if (requestorType == RequestorType.PROXY) {
                            PasswordAuthentication(username, password.toCharArray())
                        } else {
                            null
                        }
                    }
                }
            },
        )
        logger.d { "Proxy enabled: ${config.type} ${config.host}:${config.port}" }
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
