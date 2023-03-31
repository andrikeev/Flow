package flow.topic.open

import androidx.lifecycle.SavedStateHandle
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.navigation.model.NavigationArgument
import flow.navigation.model.NavigationDeepLink
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.model.appendRequiredArgs
import flow.navigation.model.buildDeepLink
import flow.navigation.model.buildRoute
import flow.navigation.require
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel

private const val TopicIdKey = "t"
private const val OpenTopicRoute = "open_topic"

context(NavigationGraphBuilder)
fun addOpenTopic(
    back: () -> Unit,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
    deepLinkUrls: List<String> = emptyList(),
    animations: NavigationAnimations,
) = addDestination(
    route = buildRoute(
        route = OpenTopicRoute,
        optionalArgsBuilder = { appendRequiredArgs(TopicIdKey) },
    ),
    arguments = listOf(
        NavigationArgument(TopicIdKey, nullable = true),
    ),
    deepLinks = deepLinkUrls.map { url ->
        NavigationDeepLink(buildDeepLink(url) { appendRequiredArgs(TopicIdKey) })
    },
    animations = animations,
) {
    OpenTopicScreen(
        viewModel = viewModel(),
        back = back,
        openTopic = { topic ->
            back()
            openTopic(topic)
        },
        openTorrent = { torrent ->
            back()
            openTorrent(torrent)
        },
    )
}

internal val SavedStateHandle.id: String
    get() = require(TopicIdKey)
