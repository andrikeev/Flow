package flow.forum.category

import flow.models.forum.Category
import flow.models.topic.Topic
import flow.models.topic.TopicModel

internal sealed interface CategoryAction {
    data object BackClick : CategoryAction
    data object BookmarkClick : CategoryAction
    data class CategoryClick(val category: Category) : CategoryAction
    data object EndOfListReached : CategoryAction
    data class FavoriteClick(val topicModel: TopicModel<out Topic>) : CategoryAction
    data object LoginClick : CategoryAction
    data object RetryClick : CategoryAction
    data object SearchClick : CategoryAction
    data class TopicClick(val topicModel: TopicModel<out Topic>) : CategoryAction
}
