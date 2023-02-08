package flow.proxy.rutracker.routes

import flow.network.api.NetworkApi
import flow.proxy.rutracker.di.inject
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

internal fun Application.configureTopicRoutes() {
    val api by inject<NetworkApi>()

    routing {
        get("/topic") {
            val id = call.request.queryParameters["id"]
            val pid = call.request.queryParameters["pid"]
            val page = call.request.queryParameters["page"]?.toIntOrNull()
            require(id != null || pid != null)
            require(page == null || page > 0)
            respond(
                api.getTopic(
                    token = call.request.authToken,
                    id = id,
                    pid = pid,
                    page = page,
                )
            )
        }

        get("/comments") {
            val id = call.request.queryParameters["id"]
            val pid = call.request.queryParameters["pid"]
            val page = call.request.queryParameters["page"]?.toIntOrNull()
            require(id != null || pid != null)
            respond(
                api.getCommentsPage(
                    token = call.request.authToken,
                    id = id,
                    pid = pid,
                    page = page
                )
            )
        }

        post("/comments/add") {
            val topicId: String by call.request.queryParameters
            val message: String by call.request.queryParameters
            require(topicId.isNotEmpty())
            require(message.isNotEmpty())
            respond(
                api.addComment(
                    token = call.request.authToken,
                    topicId = topicId,
                    message = message
                )
            )
        }
    }
}
