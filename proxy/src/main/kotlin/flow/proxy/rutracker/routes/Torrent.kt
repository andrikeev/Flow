package flow.proxy.rutracker.routes

import flow.network.api.NetworkApi
import flow.network.dto.ResultDto
import flow.proxy.rutracker.di.inject
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

internal fun Application.configureTorrentRoutes() {
    val api by inject<NetworkApi>()

    routing {
        get("/torrent") {
            respond(
                api.getTorrent(
                    token = call.request.authToken,
                    id = call.request.queryParameters.require("id"),
                )
            )
        }

        get("/download") {
            when (
                val result = api.download(
                    token = call.request.authToken,
                    id = call.request.queryParameters.require("id"),
                )
            ) {
                is ResultDto.Data -> {
                    val (contentDisposition, contentType, bytes) = result.value
                    call.response.header("Content-Disposition", contentDisposition)
                    call.respondBytes(
                        status = HttpStatusCode.OK,
                        bytes = bytes,
                        contentType = ContentType.parse(contentType),
                    )
                }

                else -> respond(result)
            }
        }
    }
}
