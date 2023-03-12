package flow.proxy.rutracker.routes

import flow.network.api.NetworkApi
import flow.proxy.rutracker.di.inject
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

internal fun Application.configureAuthRoutes() {
    val api by inject<NetworkApi>()

    routing {
        post("/login") {
            with(call.receiveParameters()) {
                respond(
                    api.login(
                        username = require("username"),
                        password = require("password"),
                        captchaSid = getOrEmpty("cap_sid"),
                        captchaCode = getOrEmpty("cap_code"),
                        captchaValue = getOrEmpty("cap_val"),
                    )
                )
            }
        }
    }
}
