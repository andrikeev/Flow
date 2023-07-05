package flow.topic

import androidx.lifecycle.SavedStateHandle
import flow.models.search.Filter
import flow.navigation.NavigationController
import flow.navigation.model.NavigationArgument
import flow.navigation.model.NavigationDeepLink
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.model.appendOptionalArgs
import flow.navigation.model.appendRequiredArgs
import flow.navigation.model.appendRequiredParams
import flow.navigation.model.buildDeepLink
import flow.navigation.model.buildRoute
import flow.navigation.require
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel

private const val TopicIdKey = "t"
private const val TopicRoute = "topic"

context(NavigationGraphBuilder)
fun addTopic(
    back: () -> Unit,
    openCategory: (id: String) -> Unit,
    openLogin: () -> Unit,
    openSearch: (filter: Filter) -> Unit,
    deepLinkUrls: List<String> = emptyList(),
    animations: NavigationAnimations,
) = addDestination(
    route = buildRoute(
        route = TopicRoute,
        optionalArgsBuilder = { appendRequiredArgs(TopicIdKey) },
    ),
    arguments = listOf(NavigationArgument(TopicIdKey)),
    deepLinks = deepLinkUrls.map { url ->
        NavigationDeepLink(buildDeepLink(url) { appendOptionalArgs(TopicIdKey) })
    },
    animations = animations,
) {
    TopicScreen(
        viewModel = viewModel(),
        back = back,
        openCategory = openCategory,
        openLogin = openLogin,
        openSearch = openSearch,
    )
}

context(NavigationGraphBuilder, NavigationController)
fun openTopic(id: String) {
    navigate(
        buildRoute(
            route = TopicRoute,
            requiredArgsBuilder = { appendRequiredParams(id) },
        ),
    )
}

internal val SavedStateHandle.id: String
    get() = require(TopicIdKey)
