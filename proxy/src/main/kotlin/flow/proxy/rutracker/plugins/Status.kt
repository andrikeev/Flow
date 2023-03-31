package flow.proxy.rutracker.plugins

import flow.network.model.BadRequest
import flow.network.model.FlowProxyError
import flow.network.model.Forbidden
import flow.network.model.NoConnection
import flow.network.model.NoData
import flow.network.model.NotFound
import flow.network.model.Unauthorized
import flow.network.model.Unknown
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.logError
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import java.io.IOException

internal fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logError(call, cause)
            when (cause) {
                is IOException -> call.respond(status = HttpStatusCode.GatewayTimeout, message = Unit)
                is IllegalStateException -> call.respond(status = HttpStatusCode.BadRequest, message = Unit)
                is IllegalArgumentException -> call.respond(status = HttpStatusCode.BadRequest, message = Unit)
                is MissingRequestParameterException -> call.respond(status = HttpStatusCode.BadRequest, message = Unit)
                is FlowProxyError -> {
                    when (cause) {
                        BadRequest -> call.respond(status = HttpStatusCode.BadRequest, message = Unit)
                        Forbidden -> call.respond(status = HttpStatusCode.Forbidden, message = Unit)
                        NoConnection -> call.respond(status = HttpStatusCode.BadGateway, message = Unit)
                        NoData -> call.respond(status = HttpStatusCode.NoContent, message = Unit)
                        NotFound -> call.respond(status = HttpStatusCode.NotFound, message = Unit)
                        Unauthorized -> call.respond(status = HttpStatusCode.Unauthorized, message = Unit)
                        Unknown -> call.respond(status = HttpStatusCode.InternalServerError, message = Unit)
                    }
                }
                else -> call.respond(status = HttpStatusCode.InternalServerError, message = Unit)
            }
        }
    }
}
