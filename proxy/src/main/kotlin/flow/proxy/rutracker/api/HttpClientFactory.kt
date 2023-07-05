package flow.proxy.rutracker.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging

internal object HttpClientFactory {
    private const val DefaultUrl = "https://rutracker.org/forum/"

    fun create(): HttpClient = HttpClient(CIO) {
        defaultRequest { url(DefaultUrl) }
        install(Logging)
    }
}
