package flow.navigation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import flow.designsystem.component.Scaffold
import flow.navigation.LocalDeepLinks
import flow.navigation.NavigationController
import flow.navigation.NestedNavigationController
import flow.navigation.canPopBackAsState
import flow.navigation.currentTopLevelRouteAsState
import flow.navigation.model.NavigationBarItem
import flow.navigation.model.NavigationGraphBuilder

@Composable
fun MobileNavigation(
    navigationController: NavigationController,
    navigationGraphBuilder: NavigationGraphBuilder.() -> Unit,
) = Scaffold { padding ->
    NavigationHost(
        modifier = Modifier.padding(padding),
        navigationController = navigationController,
        navigationGraphBuilder = navigationGraphBuilder,
    )
    val deepLinks = LocalDeepLinks.current
    LaunchedEffect(deepLinks.initialDeepLink) {
        deepLinks.initialDeepLink
            ?.let(navigationController::deeplink)
    }
    LaunchedEffect(deepLinks.deepLink) {
        deepLinks.deepLink
            ?.let(navigationController::deeplink)
    }
}

@Composable
fun NestedMobileNavigation(
    navigationController: NestedNavigationController,
    navigationBarItems: List<NavigationBarItem>,
    navigationGraphBuilder: NavigationGraphBuilder.() -> Unit,
) {
    val backHandlerEnabled by navigationController.canPopBackAsState()
    BackHandler(
        enabled = backHandlerEnabled,
        onBack = navigationController::popBackStack,
    )
    Scaffold(
        content = { padding ->
            NavigationHost(
                modifier = Modifier.padding(padding),
                navigationController = navigationController,
                navigationGraphBuilder = navigationGraphBuilder,
            )
        },
        bottomBar = {
            val currentGraphRoute by navigationController.currentTopLevelRouteAsState()
            BottomNavigation(
                items = navigationBarItems,
                selected = currentGraphRoute,
                onClick = navigationController::navigateTopLevel,
            )
        },
    )
}
