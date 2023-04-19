package flow.navigation

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import flow.logger.api.LoggerFactory
import flow.ui.platform.LocalLoggerFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface NavigationController {
    val navHostController: NavHostController
    fun navigate(route: String, args: Bundle.() -> Unit = {})
    fun popBackStack(): Boolean
}

interface DeeplinkNavigationController : NavigationController {
    fun handleDeepLink(intent: Intent?): Boolean
}

interface NestedNavigationController : NavigationController {
    fun navigateTopLevel(route: String)
    val currentTopLevelRouteFlow: Flow<String>
}

private open class NavigationControllerImpl(
    override val navHostController: NavHostController,
    loggerFactory: LoggerFactory,
) : NavigationController {

    protected val logger = loggerFactory.get("NavigationController")

    protected val currentRoute: String?
        get() = navHostController.currentDestination?.route

    protected val currentGraph: String?
        get() = navHostController.currentDestination?.parent?.route

    override fun navigate(route: String, args: Bundle.() -> Unit) {
        logger.d { "navigate: route=$route" }
        navHostController.navigate(route = route, args = args)
    }

    override fun popBackStack(): Boolean {
        logger.d { "popBackStack" }
        return navHostController.popBackStack()
    }
}

private class DeeplinkNavigationControllerImpl(
    navHostController: NavHostController,
    loggerFactory: LoggerFactory,
) : NavigationControllerImpl(navHostController, loggerFactory), DeeplinkNavigationController {
    override fun handleDeepLink(intent: Intent?): Boolean {
        logger.d { "handleDeepLink: data=${intent?.dataString}" }
        return navHostController.handleDeepLink(intent)
    }
}

private class NestedNavigationControllerImpl(
    navHostController: NavHostController,
    loggerFactory: LoggerFactory,
) : NavigationControllerImpl(navHostController, loggerFactory), NestedNavigationController {
    override fun navigateTopLevel(route: String) {
        logger.d { "navigateTopLevel: destination route=$route; current route=$currentRoute; current graph=$currentGraph" }
        navHostController.navigate(route = route) {
            popUpTo(navHostController.graph.id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    override val currentTopLevelRouteFlow: Flow<String> by lazy {
        navHostController
            .currentBackStackEntryFlow
            .map { it.destination.run { parent?.route ?: route.orEmpty() } }
    }
}

@Composable
fun rememberNavigationController(): DeeplinkNavigationController {
    val navHostController = rememberAnimatedNavController()
    val loggerFactory = LocalLoggerFactory.current
    return remember { DeeplinkNavigationControllerImpl(navHostController, loggerFactory) }
}

@Composable
fun rememberNestedNavigationController(): NestedNavigationController {
    val navHostController = rememberAnimatedNavController()
    val loggerFactory = LocalLoggerFactory.current
    return remember { NestedNavigationControllerImpl(navHostController, loggerFactory) }
}

@Composable
internal fun NestedNavigationController.currentTopLevelRouteAsState(): State<String?> {
    return currentTopLevelRouteFlow.collectAsState(null)
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
        if (deepLinkMatch.destination.arguments.isNotEmpty()) {
            navigate(route, navOptions(options))
        } else {
            val destination = deepLinkMatch.destination
            val id = destination.id
            navigate(id, Bundle().apply(args), navOptions(options))
        }
    } else {
        navigate(route, navOptions(options))
    }
}
