package flow.navigation.model

import androidx.compose.runtime.Composable
import flow.navigation.ui.NavigationAnimations

interface NavigationGraphBuilder {
    val graph: String?
    fun addDestination(
        route: String,
        isStartRoute: Boolean = false,
        arguments: List<NavigationArgument> = emptyList(),
        animations: NavigationAnimations = NavigationAnimations.Default,
        options: NavigationOptions = NavigationOptions.Empty,
        content: @Composable () -> Unit,
    )

    fun addGraph(
        route: String,
        isStartRoute: Boolean = false,
        animations: NavigationAnimations = NavigationAnimations.Default,
        nestedDestinations: NavigationGraphBuilder.() -> Unit,
    )

    fun route(value: String): String {
        return graph?.let { "$it/" }.orEmpty() + value
    }
}

private class NavigationGraphBuilderImpl(
    override val graph: String?,
) : NavigationGraphBuilder {
    private val destinations = mutableListOf<NavigationDestination>()
    private lateinit var startRoute: String

    override fun addDestination(
        route: String,
        isStartRoute: Boolean,
        arguments: List<NavigationArgument>,
        animations: NavigationAnimations,
        options: NavigationOptions,
        content: @Composable () -> Unit,
    ) {
        destinations.add(NavigationDestination.Destination(route, arguments, content, animations, options))
        if (isStartRoute) {
            startRoute = route
        }
    }

    override fun addGraph(
        route: String,
        isStartRoute: Boolean,
        animations: NavigationAnimations,
        nestedDestinations: NavigationGraphBuilder.() -> Unit,
    ) {
        val (startRoute, destinations) = buildNavigationGraph(route, nestedDestinations)
        this.destinations.add(
            NavigationDestination.Graph(
                route = route,
                startRoute = startRoute,
                destinations = destinations,
                animations = animations,
            )
        )
        if (isStartRoute) {
            this.startRoute = route
        }
    }

    fun build(): NavigationGraph {
        return NavigationGraph(
            startRoute = startRoute,
            destinations = destinations,
        )
    }
}

internal fun buildNavigationGraph(
    graph: String? = null,
    destinationsBuilder: NavigationGraphBuilder.() -> Unit,
) = NavigationGraphBuilderImpl(graph).apply(destinationsBuilder).build()
