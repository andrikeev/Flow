package flow.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import flow.navigation.NavigationController
import flow.navigation.model.NavigationDestination
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.model.buildNavigationGraph

@Composable
internal fun NavigationHost(
    modifier: Modifier = Modifier,
    navigationController: NavigationController,
    navigationGraphBuilder: NavigationGraphBuilder.() -> Unit,
) {
    val (startRoute, destinations) = buildNavigationGraph(destinationsBuilder = navigationGraphBuilder)
    AnimatedNavHost(
        modifier = modifier,
        navController = navigationController.navHostController,
        startDestination = startRoute,
        builder = { destinations.forEach(::add) },
    )
}

internal fun NavGraphBuilder.add(destination: NavigationDestination) {
    when (destination) {
        is NavigationDestination.Graph -> navigation(
            route = destination.route,
            startDestination = destination.startRoute,
            builder = { destination.destinations.forEach(this::add) },
            enterTransition = destination.animations.enterTransition.toEnterTransition(),
            exitTransition = destination.animations.exitTransition.toExitTransition(),
            popEnterTransition = destination.animations.popEnterTransition.toEnterTransition(),
            popExitTransition = destination.animations.popExitTransition.toExitTransition(),
        )
        is NavigationDestination.Destination -> composable(
            route = destination.route,
            content = { destination.content() },
            enterTransition = destination.animations.enterTransition.toEnterTransition(),
            exitTransition = destination.animations.exitTransition.toExitTransition(),
            popEnterTransition = destination.animations.popEnterTransition.toEnterTransition(),
            popExitTransition = destination.animations.popExitTransition.toExitTransition(),
        )
    }
}
