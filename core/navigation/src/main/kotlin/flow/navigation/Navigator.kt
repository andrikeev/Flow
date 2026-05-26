package flow.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import flow.logger.api.LoggerFactory
import flow.ui.platform.LocalLoggerFactory

class Navigator internal constructor(
    val state: NavigationState,
    private val deepLinkResolver: (Uri) -> NavKey?,
    loggerFactory: LoggerFactory,
) {
    private val logger = loggerFactory.get("Navigator")

    fun navigate(route: NavKey) {
        logger.d { "navigate: route=$route" }
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            state.currentBackStack.add(route)
        }
    }

    fun popBackStack(): Boolean {
        logger.d { "popBackStack" }
        val stack = state.currentBackStack
        return when {
            stack.size > 1 -> {
                stack.removeLastOrNull() != null
            }
            state.topLevelRoute != state.startRoute -> {
                state.topLevelRoute = state.startRoute
                true
            }
            else -> false
        }
    }

    fun deeplink(uri: Uri) {
        logger.d { "deeplink: uri=$uri" }
        val route = deepLinkResolver(uri) ?: return
        navigate(route)
    }
}

@Composable
fun rememberNavigator(
    state: NavigationState,
    deepLinkResolver: (Uri) -> NavKey? = { null },
): Navigator {
    val loggerFactory = LocalLoggerFactory.current
    return remember(state) { Navigator(state, deepLinkResolver, loggerFactory) }
}
