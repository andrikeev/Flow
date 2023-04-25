package flow.network.data

import flow.data.api.repository.SettingsRepository
import flow.models.settings.Endpoint
import flow.network.api.NetworkApi
import flow.network.api.RuTrackerApiFactory
import flow.network.impl.ProxyNetworkApi
import flow.network.serialization.JsonFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import okhttp3.OkHttpClient
import javax.inject.Inject

internal class NetworkApiRepositoryImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val okHttpClient: OkHttpClient,
    private val networkLogger: NetworkLogger,
) : NetworkApiRepository {
    private val apiMap = mapOf(
        Endpoint.Proxy to proxyApi(Endpoint.Proxy.host),
        Endpoint.RutrackerOrg to rutrackerApi(Endpoint.RutrackerOrg.host),
        Endpoint.RutrackerNet to rutrackerApi(Endpoint.RutrackerNet.host),
    )

    override suspend fun getAvailableApiList(): Collection<NetworkApi> {
        return apiMap.values
    }

    override suspend fun getApi(): NetworkApi {
        return apiMap.getValue(settingsRepository.getSettings().endpoint)
    }

    override suspend fun getDownloadUri(id: String): String {
        return when (val endpoint = endpoint()) {
            Endpoint.Proxy -> "https://${endpoint.host}/download/$id"
            Endpoint.RutrackerOrg -> "https://${endpoint.host}/forum/dl.php?t=$id"
            Endpoint.RutrackerNet -> "https://${endpoint.host}/forum/dl.php?t=$id"
        }
    }

    override suspend fun getAuthHeader(token: String): Pair<String, String> {
        return when (endpoint()) {
            Endpoint.Proxy -> "Auth-Token" to token
            Endpoint.RutrackerOrg -> "Cookie" to token
            Endpoint.RutrackerNet -> "Cookie" to token
        }
    }

    private suspend fun endpoint() = settingsRepository.getSettings().endpoint

    private fun proxyApi(host: String): NetworkApi {
        return ProxyNetworkApi(
            HttpClient(OkHttp) {
                engine { preconfigured = okHttpClient }
                defaultRequest { url(scheme = "https", host = host) }
                install(Logging) {
                    logger = networkLogger
                    level = LogLevel.INFO
                }
                install(ContentNegotiation) {
                    json(JsonFactory.create())
                }
            }
        )
    }

    private fun rutrackerApi(host: String): NetworkApi {
        return RuTrackerApiFactory.create(
            HttpClient(OkHttp) {
                engine { preconfigured = okHttpClient }
                defaultRequest { url("https://$host/forum/") }
                install(Logging) {
                    logger = networkLogger
                    level = LogLevel.INFO
                }
            }
        )
    }
}
