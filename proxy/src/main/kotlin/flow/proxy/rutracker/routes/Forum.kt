package flow.proxy.rutracker.routes

import flow.network.api.NetworkApi
import flow.proxy.rutracker.di.inject
import io.ktor.server.application.*
import io.ktor.server.routing.*

internal fun Application.configureForumRoutes() {
    val api by inject<NetworkApi>()

    routing {
        get("/forum") {
            respond(api.getForum())
        }

        get("/category") {
            val id = call.request.queryParameters.require("id")
            require(id.isNotEmpty())
            val page = call.request.queryParameters["page"]?.toIntOrNull()
            require(page == null || page > 0)
            respond(api.getCategory(id = id, page = page))
        }
    }
}
