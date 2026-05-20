package flow.network.data

import flow.network.api.NetworkApi
import flow.network.api.RuTrackerApiFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import okhttp3.OkHttpClient
import javax.inject.Inject

internal class NetworkApiRepositoryImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val networkLogger: NetworkLogger,
) : NetworkApiRepository {
    private val api: NetworkApi by lazy { rutrackerApi() }

    override suspend fun getApi(): NetworkApi = api

    override suspend fun getCaptchaUrl(url: String): String = url

    override suspend fun getDownloadUri(id: String): String =
        "https://rutracker.org/forum/dl.php?t=$id"

    override suspend fun getAuthHeader(token: String): Pair<String, String> =
        "Cookie" to token

    private fun rutrackerApi(): NetworkApi {
        return RuTrackerApiFactory.create(
            HttpClient(OkHttp) {
                engine { preconfigured = okHttpClient }
                defaultRequest { url("https://rutracker.org/forum/") }
                install(Logging) {
                    logger = networkLogger
                    level = LogLevel.ALL
                }
            },
        )
    }
}
