package flow.navigation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import flow.designsystem.component.Scaffold
import flow.navigation.NavigationController
import flow.navigation.model.NavigationBarItem
import flow.navigation.model.NavigationGraphBuilder
import flow.ui.component.LocalSnackbarHostState

@Composable
fun MobileNavigation(
    navigationController: NavigationController,
    navigationGraphBuilder: NavigationGraphBuilder.() -> Unit,
) {
    val snackbarState = remember { SnackbarHostState() }
    CompositionLocalProvider(LocalSnackbarHostState provides snackbarState) {
        Scaffold(snackbarHost = { SnackbarHost(snackbarState) }) { padding ->
            NavigationHost(
                modifier = Modifier.padding(padding),
                navigationController = navigationController,
                navigationGraphBuilder = navigationGraphBuilder,
            )
        }
    }
}

@Composable
fun NestedMobileNavigation(
    navigationController: NavigationController,
    navigationBarItems: List<NavigationBarItem>,
    navigationGraphBuilder: NavigationGraphBuilder.() -> Unit,
) {
    val snackbarState = remember { SnackbarHostState() }
    CompositionLocalProvider(LocalSnackbarHostState provides snackbarState) {
        Scaffold(
            content = { padding ->
                NavigationHost(
                    modifier = Modifier.padding(padding),
                    navigationController = navigationController,
                    navigationGraphBuilder = navigationGraphBuilder,
                )
            },
            snackbarHost = { SnackbarHost(snackbarState) },
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
}
