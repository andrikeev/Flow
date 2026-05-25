package flow.proxy.rutracker.routes

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.routing

internal fun Application.configureStaticRoutes() {
    routing {
        staticResources("/", "static")
    }
}
