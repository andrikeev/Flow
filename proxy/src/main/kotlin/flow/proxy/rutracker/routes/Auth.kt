package flow.proxy.rutracker.routes

import flow.network.api.NetworkApi
import flow.proxy.rutracker.di.inject
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail

internal fun Application.configureAuthRoutes() {
    val api by inject<NetworkApi>()

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
                    )
                )
            }
        }
    }
}
