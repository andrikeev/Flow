package flow.forum.category

import flow.models.forum.Category
import flow.models.topic.Topic
import flow.models.topic.TopicModel

internal sealed interface CategoryAction {
    object BackClick : CategoryAction
    object BookmarkClick : CategoryAction
    data class CategoryClick(val category: Category) : CategoryAction
    object EndOfListReached : CategoryAction
    data class FavoriteClick(val topicModel: TopicModel<out Topic>) : CategoryAction
    object LoginClick : CategoryAction
    object RetryClick : CategoryAction
    object SearchClick : CategoryAction
    data class TopicClick(val topicModel: TopicModel<out Topic>) : CategoryAction
}
