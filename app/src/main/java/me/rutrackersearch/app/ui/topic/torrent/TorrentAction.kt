package me.rutrackersearch.app.ui.topic.torrent

import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.entity.topic.Author
import me.rutrackersearch.domain.entity.topic.Topic
import me.rutrackersearch.domain.entity.topic.Torrent

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
