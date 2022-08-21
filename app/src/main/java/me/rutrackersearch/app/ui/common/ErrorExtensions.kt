package me.rutrackersearch.app.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import me.rutrackersearch.app.R
import me.rutrackersearch.models.error.Failure

@StringRes
fun Throwable.getStringRes(): Int = when (this) {
    is me.rutrackersearch.models.error.Failure.BadRequest -> R.string.error_bad_response
    is me.rutrackersearch.models.error.Failure.Blocked -> R.string.error_blocked
    is me.rutrackersearch.models.error.Failure.ConnectionError -> R.string.error_no_internet
    is me.rutrackersearch.models.error.Failure.NotAuthorized -> R.string.error_bad_response
    is me.rutrackersearch.models.error.Failure.NotFound -> R.string.error_not_found
    is me.rutrackersearch.models.error.Failure.ParseError -> R.string.error_bad_response
    is me.rutrackersearch.models.error.Failure.ServerError -> R.string.error_proxy_server
    is me.rutrackersearch.models.error.Failure.ServiceUnavailable -> R.string.error_site_connection
    else -> R.string.error_something_goes_wrong
}

@DrawableRes
fun Throwable.getIllRes(): Int = when (this) {
    is me.rutrackersearch.models.error.Failure.BadRequest -> R.drawable.ill_error_canceled
    is me.rutrackersearch.models.error.Failure.Blocked -> R.drawable.ill_error_canceled
    is me.rutrackersearch.models.error.Failure.ConnectionError -> R.drawable.ill_no_signal
    is me.rutrackersearch.models.error.Failure.NotAuthorized -> R.drawable.ill_auth_requried
    is me.rutrackersearch.models.error.Failure.NotFound -> R.drawable.ill_not_found
    is me.rutrackersearch.models.error.Failure.ParseError -> R.drawable.ill_error
    is me.rutrackersearch.models.error.Failure.ServerError -> R.drawable.ill_error
    is me.rutrackersearch.models.error.Failure.ServiceUnavailable -> R.drawable.ill_error_canceled
    else -> R.drawable.ill_error
}
