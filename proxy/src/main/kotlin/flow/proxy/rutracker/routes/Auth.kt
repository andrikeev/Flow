package flow.proxy.rutracker.routes

import flow.network.api.NetworkApi
import flow.proxy.rutracker.di.inject
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import kotlin.io.encoding.Base64

internal fun Application.configureAuthRoutes() {
    val api by inject<NetworkApi>()
    val httpClient by inject<HttpClient>()

    routing {
        post("/login") {
            with(call.receiveParameters()) {
                call.respond(
                    api.login(
                        username = getOrFail("username"),
                        password = getOrFail("password"),
                        captchaSid = get("cap_sid"),
                        captchaCode = get("cap_code"),
                        captchaValue = get("cap_val"),
                    ),
                )
            }
        }

        get("/captcha/{path}") {
            val url = Base64.UrlSafe.decode(call.parameters.getOrFail("path")).decodeToString()
            val response = httpClient.get(url)
            call.respondBytes(
                status = HttpStatusCode.OK,
                contentType = response.contentType(),
                bytes = response.readBytes(),
            )
        }
    }
}
