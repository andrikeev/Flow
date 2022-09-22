package me.rutrackersearch.app.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import me.rutrackersearch.app.R

@StringRes
fun Throwable.getStringRes(): Int = R.string.error_something_goes_wrong

@DrawableRes
fun Throwable.getIllRes(): Int = R.drawable.ill_error
