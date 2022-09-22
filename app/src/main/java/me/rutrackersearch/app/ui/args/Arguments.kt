package me.rutrackersearch.app.ui.args

import android.os.Bundle
import androidx.navigation.NavController

fun NavController.arguments(args: Bundle.() -> Unit) {
    currentBackStackEntry?.arguments?.apply(args)
}
