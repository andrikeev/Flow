package me.rutrackersearch.app.ui.args

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavController

fun NavController.arguments(args: Bundle.() -> Unit) {
    currentBackStackEntry?.arguments?.apply(args)
}

fun NavController.addArgument(key: String, value: Parcelable) {
    arguments { putParcelable(key, value) }
}
