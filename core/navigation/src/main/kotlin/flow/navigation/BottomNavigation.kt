package flow.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import flow.designsystem.component.NavigationBar
import flow.designsystem.component.NavigationBarItem

@Composable
fun BottomNavigation(
    navigationController: NavigationController,
    items: List<NavigationBarItem>,
) {
    val currentBackStackEntry by navigationController.navHostController.currentBackStackEntryAsState()
    val navigationBarRoutes = remember { items.map(NavigationBarItem::route) }
    val currentGraphRoute by remember { derivedStateOf { currentBackStackEntry?.topLevelRoute } }
    val showNavigationBar by remember { derivedStateOf { navigationBarRoutes.contains(currentGraphRoute) } }
    AnimatedVisibility(
        visible = showNavigationBar,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        BottomNavigation(
            items = items,
            selected = { route -> currentGraphRoute == route },
            onClick = { route ->
                if (navigationController.navHostController.currentDestination?.route != route) {
                    navigationController.navHostController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(navigationController.navHostController.graph.id)
                    }
                }
            },
        )
    }
}

@Composable
private fun BottomNavigation(
    items: List<NavigationBarItem>,
    selected: (route: String) -> Boolean,
    onClick: (route: String) -> Unit,
) = NavigationBar {
    items.forEach { tab ->
        NavigationBarItem(
            icon = tab.icon,
            label = stringResource(tab.labelResId),
            selected = selected(tab.route),
            onClick = { onClick(tab.route) },
        )
    }
}

private val NavBackStackEntry.topLevelRoute: String?
    get() = destination.parent?.route ?: destination.route
