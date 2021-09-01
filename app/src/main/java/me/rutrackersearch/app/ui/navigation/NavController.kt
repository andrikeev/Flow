package me.rutrackersearch.app.ui.navigation

import android.os.Parcelable
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

fun NavController.navigate(
    route: String,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    args: List<Pair<String, Parcelable>> = emptyList(),
) {
    navigate(route, navOptions, navigatorExtras)
    currentBackStackEntry?.arguments?.apply {
        args.forEach { putParcelable(it.first, it.second) }
    }
}
