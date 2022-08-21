package me.rutrackersearch.app.ui.forum.category

import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.forum.CategoryModel
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent

sealed interface CategoryAction {
    object BackClick : CategoryAction
    object RetryClick : CategoryAction
    object EndOfListReached : CategoryAction
    data class SearchClick(val category: Category) : CategoryAction
    data class CategoryClick(val category: Category) : CategoryAction
    data class TopicClick(val topic: Topic) : CategoryAction
    data class TorrentClick(val torrent: Torrent) : CategoryAction
    data class FavoriteClick(val topicModel: TopicModel<out Topic>) : CategoryAction
    data class BookmarkClick(val category: CategoryModel) : CategoryAction
}
