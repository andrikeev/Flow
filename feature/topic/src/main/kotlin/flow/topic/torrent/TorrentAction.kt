package flow.topic.torrent

import flow.models.forum.Category
import flow.models.topic.Author

internal sealed interface TorrentAction {
    data class AuthorClick(val author: Author) : TorrentAction
    object BackClick : TorrentAction
    data class CategoryClick(val category: Category) : TorrentAction
    object CommentsClick : TorrentAction
    object FavoriteClick : TorrentAction
    object LoginClick : TorrentAction
    data class MagnetClick(val link: String) : TorrentAction
    data class OpenFileClick(val uri: String) : TorrentAction
    object ShareClick : TorrentAction
    data class TorrentFileClick(val title: String) : TorrentAction
}
