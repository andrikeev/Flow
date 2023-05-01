package flow.topic.torrent

import flow.models.forum.Category
import flow.models.topic.Author
import flow.models.topic.TorrentDescription
import flow.models.topic.TorrentStatus

internal data class TorrentScreenState(
    val favoriteState: TorrentFavoriteState = TorrentFavoriteState.Initial,
    val downloadState: DownloadState = DownloadState.Initial,
)

internal sealed interface TorrentFavoriteState {
    object Initial : TorrentFavoriteState
    data class FavoriteState(val favorite: Boolean) : TorrentFavoriteState
}

internal sealed interface DownloadState {
    data class Completed(val uri: String) : DownloadState
    object Error : DownloadState
    object Initial : DownloadState
    object Started : DownloadState
}

internal data class TorrentState(
    val title: String,
    val posterImage: String?,
    val author: Author?,
    val category: Category?,
    val status: TorrentStatus?,
    val date: Long?,
    val size: String?,
    val seeds: Int?,
    val leeches: Int?,
    val magnetLink: String?,
    val description: TorrentDescription?,
    val showMagnetLink: Boolean,
    val showTorrentFile: Boolean,
)
