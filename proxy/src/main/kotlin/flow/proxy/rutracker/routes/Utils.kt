package flow.proxy.rutracker.routes

import io.ktor.http.Parameters
import io.ktor.server.request.ApplicationRequest

/**
 * @author andrikeev on 03/10/2021.
 */
internal inline fun <reified T : Enum<T>> String.toEnumOrNull(): T {
    return enumValueOf(this)
}

internal inline val ApplicationRequest.authToken: String
    get() = headers["Auth-Token"].orEmpty()

internal fun Parameters.getOrEmpty(name: String): String = get(name).orEmpty()
