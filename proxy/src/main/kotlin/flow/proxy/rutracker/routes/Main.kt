package flow.proxy.rutracker.routes

import flow.network.api.NetworkApi
import flow.proxy.rutracker.di.inject
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

internal fun Application.configureMainRoutes() {
    val api by inject<NetworkApi>()

    routing {
        get("/") {
            call.respond(api.checkAuthorized(token = call.request.authToken))
        }
        get("/index") {
            call.respond(api.checkAuthorized(token = call.request.authToken))
        }
    }
}
