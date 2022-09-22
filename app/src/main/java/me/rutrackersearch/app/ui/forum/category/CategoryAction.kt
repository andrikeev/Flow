package me.rutrackersearch.app.ui.forum.category

import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent

sealed interface CategoryAction {
    object BackClick : CategoryAction
    object BookmarkClick : CategoryAction
    data class CategoryClick(val category: Category) : CategoryAction
    object EndOfListReached : CategoryAction
    data class FavoriteClick(val topicModel: TopicModel<out Topic>) : CategoryAction
    object RetryClick : CategoryAction
    object SearchClick : CategoryAction
    data class TopicClick(val topic: Topic) : CategoryAction
    data class TorrentClick(val torrent: Torrent) : CategoryAction
}
