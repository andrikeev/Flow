package me.rutrackersearch.app.ui.topic.torrent

import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.topic.Author

sealed interface TorrentAction {
    object BackClick : TorrentAction
    object RetryClick : TorrentAction
    object CommentsClick : TorrentAction
    object FavoriteClick : TorrentAction
    object ShareClick : TorrentAction
    object MagnetClick : TorrentAction
    object TorrentFileClick : TorrentAction
    data class CategoryClick(val value: Category) : TorrentAction
    data class AuthorClick(val value: Author) : TorrentAction
}
