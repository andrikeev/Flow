package flow.navigation.model

import androidx.compose.runtime.Composable
import flow.navigation.ui.NavigationAnimations

internal data class NavigationGraph(
    val startRoute: String,
    val destinations: List<NavigationDestination>,
)

internal sealed interface NavigationDestination {
    val route: String
    val animations: NavigationAnimations

    data class Graph(
        override val route: String,
        val startRoute: String,
        val destinations: List<NavigationDestination>,
        override val animations: NavigationAnimations = NavigationAnimations.Default,
    ) : NavigationDestination

    data class Destination(
        override val route: String,
        val arguments: List<NavigationArgument> = emptyList(),
        val deepLinks: List<NavigationDeepLink> = emptyList(),
        val content: @Composable () -> Unit,
        override val animations: NavigationAnimations = NavigationAnimations.Default,
        val options: NavigationOptions,
    ) : NavigationDestination
}
