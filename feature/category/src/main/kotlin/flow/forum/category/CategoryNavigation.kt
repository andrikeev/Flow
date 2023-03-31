package flow.forum.category

import androidx.lifecycle.SavedStateHandle
import flow.models.topic.Topic
import flow.models.topic.Torrent
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

private const val CategoryIdKey = "f"
private const val CategoryRoute = "category"

context(NavigationGraphBuilder)
fun addCategory(
    back: () -> Unit,
    openCategory: (String) -> Unit,
    openLogin: () -> Unit,
    openSearchInput: (String) -> Unit,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
    deepLinkUrls: List<String> = emptyList(),
    animations: NavigationAnimations,
) = addDestination(
    route = buildRoute(
        route = CategoryRoute,
        requiredArgsBuilder = { appendRequiredArgs(CategoryIdKey) }
    ),
    arguments = listOf(NavigationArgument(CategoryIdKey)),
    deepLinks = deepLinkUrls.map { url ->
        NavigationDeepLink(buildDeepLink(url) { appendOptionalArgs(CategoryIdKey) })
    },
    content = {
        CategoryScreen(
            back = back,
            openCategory = openCategory,
            openLogin = openLogin,
            openSearchInput = openSearchInput,
            openTopic = openTopic,
            openTorrent = openTorrent,
        )
    },
    animations = animations,
)

context(NavigationGraphBuilder, NavigationController)
fun openCategory(id: String) {
    navigate(
        buildRoute(
            route = CategoryRoute,
            requiredArgsBuilder = { appendRequiredParams(id) }
        )
    )
}

internal val SavedStateHandle.categoryId: String
    get() = require(CategoryIdKey)
