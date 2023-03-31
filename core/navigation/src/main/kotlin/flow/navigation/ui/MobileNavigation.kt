package flow.navigation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import flow.designsystem.component.Scaffold
import flow.navigation.NavigationController
import flow.navigation.NestedNavigationController
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
}

@Composable
fun NestedMobileNavigation(
    navigationController: NestedNavigationController,
    navigationBarItems: List<NavigationBarItem>,
    navigationGraphBuilder: NavigationGraphBuilder.() -> Unit,
) = Scaffold(
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
