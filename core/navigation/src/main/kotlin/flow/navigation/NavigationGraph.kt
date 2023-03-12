package flow.navigation

data class NavigationGraph(
    val startRoute: String,
    val destinations: List<NavigationDestination>,
)
