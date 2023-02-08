package flow.proxy.rutracker.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import java.io.IOException

internal fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logError(call, cause)
            when (cause) {
                is IOException -> call.respond(status = HttpStatusCode.GatewayTimeout, message = Unit)
                is IllegalStateException -> call.respond(status = HttpStatusCode.BadRequest, message = Unit)
                else -> call.respond(status = HttpStatusCode.InternalServerError, message = Unit)
            }
        }
    }
}
