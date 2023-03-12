package flow.proxy.rutracker.routes

import flow.network.dto.ResultDto
import flow.network.dto.error.FlowError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

/**
 * @author andrikeev on 03/10/2021.
 */
internal inline fun <reified T : Enum<T>> String.toEnumOrNull(): T {
    return enumValueOf(this)
}

internal inline val ApplicationRequest.authToken: String get() = headers["Auth-Token"].orEmpty()

internal fun Parameters.require(name: String): String =
    get(name) ?: throw MissingRequestParameterException(name)

internal fun Parameters.getOrEmpty(name: String): String = get(name).orEmpty()

internal suspend inline fun <T> PipelineContext<*, ApplicationCall>.respond(result: ResultDto<T>) {
    @Suppress("IMPLICIT_CAST_TO_ANY")
    call.respond(
        status = when (result) {
            is ResultDto.Data -> HttpStatusCode.OK
            is ResultDto.Error -> when (result.cause) {
                is FlowError.BadRequest -> HttpStatusCode.BadRequest
                is FlowError.Forbidden -> HttpStatusCode.Forbidden
                is FlowError.NoConnection -> HttpStatusCode.GatewayTimeout
                is FlowError.NoData -> HttpStatusCode.BadGateway
                is FlowError.NotFound -> HttpStatusCode.NotFound
                is FlowError.Unauthorized -> HttpStatusCode.Unauthorized
                is FlowError.Unknown -> HttpStatusCode.InternalServerError
            }
        },
        message = when (result) {
            is ResultDto.Data -> result.value
            is ResultDto.Error -> Unit
        } as Any,
    )
}
