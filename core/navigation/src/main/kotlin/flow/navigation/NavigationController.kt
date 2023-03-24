package flow.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class NavigationController internal constructor(
    val navHostController: NavHostController,
) {
    fun navigateTopLevel(route: String) {
        navHostController.navigate(route = route) {
            popUpTo(navHostController.graph.id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigate(route: String, args: Bundle.() -> Unit = {}) =
        navHostController.navigate(route = route, args = args)

    fun popBackStack() = navHostController.popBackStack()

    @Composable
    fun currentTopLevelRouteAsState(): State<String?> {
        val currentBackStackEntryState by navHostController.currentBackStackEntryFlow.collectAsState(null)
        return produceState<String?>(null, currentBackStackEntryState) {
            value = currentBackStackEntryState?.destination?.run { parent?.route ?: route }
        }
    }
}

@Composable
fun rememberNavigationController(): NavigationController {
    val navHostController = rememberAnimatedNavController()
    return remember { NavigationController(navHostController) }
}

private fun NavController.navigate(
    route: String,
    args: Bundle.() -> Unit = {},
    options: NavOptionsBuilder.() -> Unit = {},
) {
    val routeLink = NavDeepLinkRequest.Builder
        .fromUri(NavDestination.createRoute(route).toUri())
        .build()

    val graphs = listOfNotNull(currentBackStackEntry?.destination?.parent, graph)
    val deepLinkMatch = graphs.firstNotNullOfOrNull { it.matchDeepLink(routeLink) }
    if (deepLinkMatch != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(id, Bundle().apply(args), navOptions(options))
    } else {
        navigate(route, navOptions(options))
    }
}
