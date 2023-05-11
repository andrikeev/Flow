package flow.proxy.rutracker.routes

import flow.network.api.NetworkApi
import flow.proxy.rutracker.di.inject
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail

internal fun Application.configureTopicRoutes() {
    val api by inject<NetworkApi>()

    routing {
        get("/topic/{id}") {
            call.respond(
                api.getTopic(
                    token = call.request.authToken,
                    id = call.parameters.getOrFail("id"),
                    page = call.request.queryParameters["page"]?.toIntOrNull(),
                )
            )
        }

        get("/topic2/{id}") {
            call.respond(
                api.getTopicPage(
                    token = call.request.authToken,
                    id = call.parameters.getOrFail("id"),
                    page = call.request.queryParameters["page"]?.toIntOrNull(),
                )
            )
        }

        get("/comments/{id}") {
            call.respond(
                api.getCommentsPage(
                    token = call.request.authToken,
                    id = call.parameters.getOrFail("id"),
                    page = call.request.queryParameters["page"]?.toIntOrNull(),
                )
            )
        }

        post("/comments/{id}/add") {
            call.respond(
                api.addComment(
                    token = call.request.authToken,
                    topicId = call.parameters.getOrFail("id"),
                    message = call.receiveText(),
                )
            )
        }
    }
}
