package me.rutrackersearch.app.ui.topic.torrent

import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.topic.Author
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent

sealed interface TorrentAction {
    object BackClick : TorrentAction
    object RetryClick : TorrentAction
    data class CommentsClick(val topic: Topic) : TorrentAction
    data class FavoriteClick(val torrent: TopicModel<Torrent>) : TorrentAction
    data class ShareClick(val torrent: Torrent) : TorrentAction
    data class MagnetClick(val magnetLink: String) : TorrentAction
    object TorrentFileClick : TorrentAction
    data class CategoryClick(val category: Category) : TorrentAction
    data class AuthorClick(val author: Author) : TorrentAction
}
