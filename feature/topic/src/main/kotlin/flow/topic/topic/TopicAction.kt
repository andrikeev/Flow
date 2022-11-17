package flow.topic.topic

import flow.models.topic.Topic
import flow.models.topic.TopicModel

sealed interface TopicAction {
    object BackClick : TopicAction
    object LoginClick : TopicAction
    object RetryClick : TopicAction
    object ListTopReached : TopicAction
    object EndOfListReached : TopicAction
    data class FirstVisibleItemIndexChanged(val index: Int) : TopicAction
    data class AddComment(val comment: String) : TopicAction
    data class GoToPage(val page: Int) : TopicAction
    data class FavoriteClick(val torrent: TopicModel<Topic>) : TopicAction
}
