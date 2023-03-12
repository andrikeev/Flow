package flow.navigation

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController

interface NavigationController {
    val navHostController: NavHostController

    fun navigate(route: String, vararg args: Pair<String, Parcelable>)

    fun popBackStack(): Boolean
}

@Composable
fun rememberNavigationController(): NavigationController {
    val navHostController = rememberNavController()
    return remember {
        object : NavigationController {
            override val navHostController = navHostController

            override fun navigate(route: String, vararg args: Pair<String, Parcelable>) =
                navHostController.navigate(route = route, args = args)

            override fun popBackStack(): Boolean = navHostController.popBackStack()
        }
    }
}

private fun NavController.navigate(
    route: String,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    vararg args: Pair<String, Parcelable>,
) {
    navigate(route, navOptions, navigatorExtras)
    currentBackStackEntry?.arguments?.apply {
        args.forEach { putParcelable(it.first, it.second) }
    }
}
