package flow.topic.torrent

import flow.models.forum.Category
import flow.models.topic.Author

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
