package me.rutrackersearch.app.ui.topic.topic

import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Topic

sealed interface TopicAction {
    object BackClick : TopicAction
    object LoginClick : TopicAction
    object RetryClick : TopicAction
    object ListTopReached : TopicAction
    object EndOfListReached : TopicAction
    data class FirstVisibleItemIndexChanged(val index: Int) : TopicAction
    data class AddComment(val comment: String) : TopicAction
    data class GoToPage(val page: Int): TopicAction
    data class FavoriteClick(val torrent: TopicModel<Topic>) : TopicAction
}
