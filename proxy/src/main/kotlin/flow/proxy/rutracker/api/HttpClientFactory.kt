package flow.proxy.rutracker.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*

internal object HttpClientFactory {
    private const val DefaultUrl = "https://rutracker.org/forum/"

    fun create(): HttpClient = HttpClient(CIO) {
        defaultRequest { url(DefaultUrl) }
        install(Logging)
    }
}
