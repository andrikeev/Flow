package me.rutrackersearch.models.error

sealed class Failure(cause: Throwable? = null) : Throwable(cause) {

    data class ParseError(override val cause: Throwable? = null) : Failure(cause)

    data class ConnectionError(override val cause: Throwable? = null) : Failure(cause)

    data class ServiceUnavailable(override val cause: Throwable? = null) : Failure(cause)

    data class ServerError(override val cause: Throwable? = null) : Failure(cause)

    data class BadRequest(override val cause: Throwable? = null) : Failure(cause)

    data class NotFound(override val cause: Throwable? = null) : Failure(cause)

    data class NotAuthorized(override val cause: Throwable? = null) : Failure(cause)

    data class Blocked(override val cause: Throwable? = null) : Failure(cause)

    data class DbError(override val cause: Throwable? = null) : Failure(cause)

    data class Unknown(override val cause: Throwable? = null) : Failure(cause)
}
