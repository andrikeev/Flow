package flow.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
    navigationController: NavigationController,
    navigationGraph: NavigationGraph,
) = NavHost(
    modifier = modifier,
    navController = navigationController.navHostController,
    startDestination = navigationGraph.startRoute,
) {
    fun NavGraphBuilder.add(destination: NavigationDestination) {
        when (destination) {
            is NavigationDestination.Destination -> composable(
                route = destination.route,
                content = { destination.content(navigationController) },
            )

            is NavigationDestination.Graph -> navigation(
                route = destination.route,
                startDestination = destination.startRoute,
                builder = { destination.destinations.forEach(this::add) },
            )
        }
    }
    navigationGraph.destinations.forEach(this::add)
}
