package flow.proxy.rutracker.routes

import flow.network.api.NetworkApi
import flow.proxy.rutracker.di.inject
import io.ktor.server.application.*
import io.ktor.server.routing.*

internal fun Application.configureMainRoutes() {
    val api by inject<NetworkApi>()

    routing {
        get("/") {
            respond(api.checkAuthorized(token = call.request.authToken))
        }
        get("/index") {
            respond(api.checkAuthorized(token = call.request.authToken))
        }
    }
}
