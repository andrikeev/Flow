package flow.topic.torrent

import androidx.lifecycle.SavedStateHandle
import flow.models.search.Filter
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.navigation.NavigationController
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.model.buildRoute
import flow.navigation.require
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel
import flow.ui.parcel.TorrentWrapper

private const val TorrentKey = "torrent"
private const val TorrentRoute = "torrent"

context(NavigationGraphBuilder)
fun addTorrent(
    back: () -> Unit,
    openLogin: () -> Unit,
    openComments: (Topic) -> Unit,
    openCategory: (String) -> Unit,
    openSearch: (Filter) -> Unit,
    animations: NavigationAnimations,
) = addDestination(
    route = buildRoute(TorrentRoute),
    animations = animations,
) {
    TorrentScreen(
        viewModel = viewModel(),
        back = back,
        openLogin = openLogin,
        openComments = openComments,
        openCategory = openCategory,
        openSearch = openSearch,
    )
}

context(NavigationGraphBuilder, NavigationController)
fun openTorrent(torrent: Torrent) {
    navigate(buildRoute(TorrentRoute)) {
        putParcelable(TorrentKey, TorrentWrapper(torrent))
    }
}

internal val SavedStateHandle.torrent: Torrent get() = require<TorrentWrapper>(TorrentKey).torrent
