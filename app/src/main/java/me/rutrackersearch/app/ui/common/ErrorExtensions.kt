package me.rutrackersearch.app.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import me.rutrackersearch.app.R
import me.rutrackersearch.domain.entity.error.Failure

@StringRes
fun Throwable.getStringRes(): Int = when (this) {
    is Failure.BadRequest -> R.string.error_bad_response
    is Failure.Blocked -> R.string.error_blocked
    is Failure.ConnectionError -> R.string.error_no_internet
    is Failure.NotAuthorized -> R.string.error_bad_response
    is Failure.NotFound -> R.string.error_not_found
    is Failure.ParseError -> R.string.error_bad_response
    is Failure.ServerError -> R.string.error_proxy_server
    is Failure.ServiceUnavailable -> R.string.error_site_connection
    else -> R.string.error_something_goes_wrong
}

@DrawableRes
fun Throwable.getIllRes(): Int = when (this) {
    is Failure.BadRequest -> R.drawable.ill_error_canceled
    is Failure.Blocked -> R.drawable.ill_error_canceled
    is Failure.ConnectionError -> R.drawable.ill_no_signal
    is Failure.NotAuthorized -> R.drawable.ill_auth_requried
    is Failure.NotFound -> R.drawable.ill_not_found
    is Failure.ParseError -> R.drawable.ill_error
    is Failure.ServerError -> R.drawable.ill_error
    is Failure.ServiceUnavailable -> R.drawable.ill_error_canceled
    else -> R.drawable.ill_error
}
