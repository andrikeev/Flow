package flow.topic.torrent

import flow.models.auth.AuthState
import flow.models.topic.Torrent

data class TorrentScreenState(
    val authState: AuthState = AuthState.Unauthorized,
    val favoriteState: TorrentFavoriteState = TorrentFavoriteState.Initial,
    val torrentState: TorrentState,
    val downloadState: DownloadState = DownloadState.Initial,
)

sealed interface TorrentState {
    val torrent: Torrent

    data class Initial(
        override val torrent: Torrent,
    ) : TorrentState

    data class Loaded(
        override val torrent: Torrent,
    ) : TorrentState

    data class Error(
        override val torrent: Torrent,
    ) : TorrentState
}

sealed interface TorrentFavoriteState {
    object Initial : TorrentFavoriteState
    data class FavoriteState(val favorite: Boolean) : TorrentFavoriteState
}

sealed interface DownloadState {
    object Initial : DownloadState
    object Started : DownloadState
    data class Downloaded(val uri: String) : DownloadState
}
