package flow.navigation

import androidx.compose.runtime.Composable

private typealias DestinationContent = @Composable (NavigationController) -> Unit

sealed interface NavigationDestination {
    val route: String

    data class Graph(
        override val route: String,
        val startRoute: String,
        val destinations: List<NavigationDestination>,
    ) : NavigationDestination

    data class Destination(
        override val route: String,
        val content: DestinationContent,
    ) : NavigationDestination
}
