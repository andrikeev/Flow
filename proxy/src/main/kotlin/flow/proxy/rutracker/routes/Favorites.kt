package flow.proxy.rutracker.routes

import flow.network.api.NetworkApi
import flow.proxy.rutracker.di.inject
import io.ktor.server.application.*
import io.ktor.server.routing.*

internal fun Application.configureFavoritesRoutes() {
    val api by inject<NetworkApi>()

    routing {
        get("/favorites") {
            respond(api.getFavorites(token = call.request.authToken))
        }

        post("/favorites/add") {
            val id = call.request.queryParameters.require("id")
            require(id.isNotEmpty())
            respond(api.addFavorite(token = call.request.authToken, id = id))
        }

        post("/favorites/remove") {
            val id = call.request.queryParameters.require("id")
            require(id.isNotEmpty())
            respond(api.removeFavorite(token = call.request.authToken, id = id))
        }
    }
}
