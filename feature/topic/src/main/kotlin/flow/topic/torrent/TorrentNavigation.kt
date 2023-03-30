package flow.topic.torrent

import androidx.lifecycle.SavedStateHandle
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.navigation.NavigationController
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel
import flow.ui.args.require
import flow.ui.parcel.TorrentWrapper

private const val TorrentKey = "Torrent"

private val NavigationGraphBuilder.TorrentRoute
    get() = route("Torrent")

data class TorrentNavigation(
    val addTorrent: NavigationGraphBuilder.(
        back: () -> Unit,
        openLogin: () -> Unit,
        openComments: (Topic) -> Unit,
        openCategory: (Category) -> Unit,
        openSearch: (Filter) -> Unit,
        animations: NavigationAnimations,
    ) -> Unit,
    val openTorrent: NavigationController.(Torrent) -> Unit,
)

fun NavigationGraphBuilder.buildTorrentNavigation() = TorrentNavigation(
    addTorrent = NavigationGraphBuilder::addTorrent,
    openTorrent = { torrent ->
        navigate(TorrentRoute) {
            putParcelable(TorrentKey, TorrentWrapper(torrent))
        }
    },
)

private fun NavigationGraphBuilder.addTorrent(
    back: () -> Unit,
    openLogin: () -> Unit,
    openComments: (Topic) -> Unit,
    openCategory: (Category) -> Unit,
    openSearch: (Filter) -> Unit,
    animations: NavigationAnimations,
) = addDestination(
    route = TorrentRoute,
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

internal val SavedStateHandle.torrent: Torrent get() = require<TorrentWrapper>(TorrentKey).torrent
