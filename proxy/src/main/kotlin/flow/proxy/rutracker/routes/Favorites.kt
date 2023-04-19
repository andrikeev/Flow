package flow.proxy.rutracker.routes

import flow.network.api.NetworkApi
import flow.proxy.rutracker.di.inject
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail

internal fun Application.configureFavoritesRoutes() {
    val api by inject<NetworkApi>()

    routing {
        get("/favorites") {
            call.respond(api.getFavorites(token = call.request.authToken))
        }

        post("/favorites/add/{id}") {
            call.respond(
                api.addFavorite(
                    token = call.request.authToken,
                    id = call.parameters.getOrFail("id"),
                )
            )
        }

        post("/favorites/remove/{id}") {
            call.respond(
                api.removeFavorite(
                    token = call.request.authToken,
                    id = call.parameters.getOrFail("id"),
                )
            )
        }
    }
}
