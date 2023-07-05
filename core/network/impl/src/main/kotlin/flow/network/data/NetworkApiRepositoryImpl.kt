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
    private val apiMap = mutableMapOf<Endpoint, NetworkApi>()

    override suspend fun getApi(): NetworkApi {
        val endpoint = endpoint()
        return apiMap.getOrPut(endpoint) {
            when (endpoint) {
                is Endpoint.Proxy -> proxyApi(endpoint.host)
                is Endpoint.RutrackerEndpoint -> rutrackerApi(endpoint.host)
            }
        }
    }

    override suspend fun getDownloadUri(id: String): String {
        return when (val endpoint = endpoint()) {
            is Endpoint.Proxy -> "https://${endpoint.host}/download/$id"
            is Endpoint.RutrackerEndpoint -> "https://${endpoint.host}/forum/dl.php?t=$id"
        }
    }

    override suspend fun getAuthHeader(token: String): Pair<String, String> {
        return when (endpoint()) {
            is Endpoint.Proxy -> "Auth-Token" to token
            is Endpoint.RutrackerEndpoint -> "Cookie" to token
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
            },
        )
    }

    private fun rutrackerApi(host: String): NetworkApi {
        return RuTrackerApiFactory.create(
            HttpClient(OkHttp) {
                engine { preconfigured = okHttpClient }
                defaultRequest { url("https://$host/forum/") }
                install(Logging) {
                    logger = networkLogger
                    level = LogLevel.ALL
                }
            },
        )
    }
}
