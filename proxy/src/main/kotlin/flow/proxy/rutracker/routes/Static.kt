package flow.proxy.rutracker.routes

import io.ktor.server.application.Application
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.routing.routing

internal fun Application.configureStaticRoutes() {
    routing {
        static("/") {
            resources("static")
        }
    }
}
