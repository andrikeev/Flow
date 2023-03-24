package flow.topic.open

import androidx.lifecycle.SavedStateHandle
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.navigation.NavigationController
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel

private const val IdKey = "Id"
private const val PidKey = "Pid"

private val NavigationGraphBuilder.OpenTopicRoute
    get() = route("OpenTopic")

data class OpenTopicNavigation(
    val addOpenTopic: NavigationGraphBuilder.(
        back: () -> Unit,
        openTopic: (Topic) -> Unit,
        openTorrent: (Torrent) -> Unit,
        animations: NavigationAnimations,
    ) -> Unit,
    val openTopic: NavigationController.(id: String?, pid: String?) -> Unit,
)

fun NavigationGraphBuilder.buildOpenTopicNavigation() = OpenTopicNavigation(
    addOpenTopic = NavigationGraphBuilder::addOpenTopic,
    openTopic = { id, pid ->
        navigate(OpenTopicRoute) {
            putString(IdKey, id)
            putString(PidKey, pid)
        }
    },
)

private fun NavigationGraphBuilder.addOpenTopic(
    back: () -> Unit,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
    animations: NavigationAnimations,
) = addDestination(
    route = OpenTopicRoute,
    animations = animations,
) {
    OpenTopicScreen(
        viewModel = viewModel(),
        back = back,
        openTopic = openTopic,
        openTorrent = openTorrent,
    )
}

internal val SavedStateHandle.id: String get() = get<String?>(IdKey).orEmpty()
internal val SavedStateHandle.pid: String get() = get<String?>(PidKey).orEmpty()
