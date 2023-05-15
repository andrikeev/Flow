package flow.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import flow.logger.api.LoggerFactory
import flow.ui.platform.LocalLoggerFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface NavigationController {
    val navHostController: NavHostController
    fun navigate(route: String)
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

    protected val currentGraphStartRoute: String?
        get() = navHostController.currentDestination?.parent?.startDestinationRoute

    override fun navigate(route: String) {
        logger.d { "navigate: route=$route" }
        navHostController.navigate(route = route)
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
        logger.d { "navigateTopLevel: destination route=$route; " +
                "current route=$currentRoute; " +
                "current graph=$currentGraph; " +
                "current graph start route = $currentGraphStartRoute"}
        if (route == currentGraph) {
            if (currentRoute != currentGraphStartRoute) {
                navHostController.navigate(route = route) {
                    popUpTo(navHostController.graph.id) { saveState = true }
                    launchSingleTop = true
                }
            }
        } else if (route != currentRoute) {
            navHostController.navigate(route = route) {
                popUpTo(navHostController.graph.id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
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
