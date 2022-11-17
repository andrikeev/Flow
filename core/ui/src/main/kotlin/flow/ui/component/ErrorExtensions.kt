package flow.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import flow.ui.R

@StringRes
fun Throwable?.getStringRes(): Int = R.string.error_something_goes_wrong

@DrawableRes
fun Throwable?.getIllRes(): Int = R.drawable.ill_error
