package me.rutrackersearch.data.converters

import me.rutrackersearch.domain.entity.error.Failure
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.ProtocolException
import java.net.SocketException
import java.net.SocketTimeoutException

fun Throwable.toFailure(): Failure {
    return when (this) {
        is HttpException -> {
            when (code()) {
                400 -> Failure.BadRequest(this)
                401 -> Failure.NotAuthorized(this)
                403 -> Failure.Blocked(this)
                404 -> Failure.NotFound(this)
                500 -> Failure.ServerError(this)
                502 -> Failure.ServiceUnavailable(this)
                504 -> Failure.ServiceUnavailable(this)
                else -> Failure.Unknown(this)
            }
        }
        is UnsupportedEncodingException -> Failure.BadRequest(this)
        is ProtocolException -> Failure.BadRequest(this)
        is IOException -> Failure.ConnectionError(this)
        is JSONException -> Failure.ParseError(this)
        is SocketException,
        is SocketTimeoutException -> Failure.ConnectionError(this)
        else -> Failure.Unknown(this)
    }
}
