package flow.navigation

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
import kotlinx.coroutines.flow.onEach
import java.util.Stack

interface NavigationController {
    val navHostController: NavHostController
    fun navigate(route: String)
    fun popBackStack(): Boolean
}

interface NestedNavigationController : NavigationController {
    fun navigateTopLevel(route: String)
    val currentTopLevelRouteFlow: Flow<String>
    val canPopBackFlow: Flow<Boolean>
}

private open class NavigationControllerImpl(
    override val navHostController: NavHostController,
    loggerFactory: LoggerFactory,
) : NavigationController {

    private val logger = loggerFactory.get("NavigationController")

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
        return navHostController.popBackStack().also {
            logger.d { "popBackStack: handled=$it" }
        }
    }
}

private class NestedNavigationControllerImpl(
    navHostController: NavHostController,
    loggerFactory: LoggerFactory,
) : NavigationControllerImpl(navHostController, loggerFactory), NestedNavigationController {

    private val logger = loggerFactory.get("NestedNavigationController")

    private val startTopLevelRoute: String by lazy {
        requireNotNull(navHostController.graph.startDestinationRoute)
    }
    private val topLevelBackStack = Stack<String>()
    private var topLevelRoute: String = ""

    override fun navigateTopLevel(route: String) {
        logger.d { "navigateTopLevel: route=$route" }
        if (route == currentGraph && currentRoute != currentGraphStartRoute) {
            navigate(
                route = route,
                addBackStack = true,
                retain = false,
            )
        } else if (route != currentRoute) {
            navigate(
                route = route,
                addBackStack = true,
                retain = true,
            )
        }
    }

    override val currentTopLevelRouteFlow: Flow<String> by lazy {
        navHostController
            .currentBackStackEntryFlow
            .map { it.destination.run { parent?.route ?: route.orEmpty() } }
            .onEach { logger.d { "currentTopLevelRoute: $it" } }
    }

    override val canPopBackFlow: Flow<Boolean> by lazy {
        navHostController
            .currentBackStackEntryFlow
            .map { topLevelBackStack.isNotEmpty() || !isGraphRoot() }
            .onEach { logger.d { "canPopBack: $it" } }
    }

    override fun popBackStack(): Boolean {
        return when {
            navHostController.popBackStack() -> true
            topLevelBackStack.isNotEmpty() -> {
                navigate(
                    route = topLevelBackStack.pop(),
                    addBackStack = false,
                    retain = true,
                )
                true
            }

            else -> {
                if (isGraphRoot()) {
                    false
                } else {
                    navigate(
                        route = startTopLevelRoute,
                        addBackStack = false,
                        retain = true,
                    )
                    true
                }
            }
        }.also { logger.d { "popBackStack: handled=$it" } }
    }

    private fun isGraphRoot() = topLevelRoute == startTopLevelRoute

    private fun navigate(
        route: String,
        addBackStack: Boolean,
        retain: Boolean,
    ) {
        logger.d { "navigate: route=$route; addHistory=$addBackStack; retain=$retain" }
        navHostController.navigate(route = route) {
            popUpTo(navHostController.graph.id) { saveState = retain }
            launchSingleTop = true
            restoreState = retain
        }
        if (addBackStack && topLevelRoute.isNotBlank()) {
            topLevelBackStack.remove(topLevelRoute)
            topLevelBackStack.push(topLevelRoute)
        }
        topLevelBackStack.remove(route)
        topLevelRoute = route
    }
}

@Composable
fun rememberNavigationController(): NavigationController {
    val navHostController = rememberAnimatedNavController()
    val loggerFactory = LocalLoggerFactory.current
    return remember { NavigationControllerImpl(navHostController, loggerFactory) }
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

@Composable
internal fun NestedNavigationController.canPopBackAsState(): State<Boolean> {
    return canPopBackFlow.collectAsState(false)
}
