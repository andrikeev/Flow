package flow.proxy.rutracker.plugins

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

internal fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson()
    }
}
