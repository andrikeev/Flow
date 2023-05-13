package flow.search.result

import flow.models.forum.Category
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.models.topic.Author
import flow.models.topic.Topic
import flow.models.topic.TopicModel

internal sealed interface SearchResultAction {
    data class FavoriteClick(val topicModel: TopicModel<out Topic>) : SearchResultAction
    data class SetAuthor(val author: Author?) : SearchResultAction
    data class SetCategories(val categories: List<Category>?) : SearchResultAction
    data class SetOrder(val order: Order) : SearchResultAction
    data class SetPeriod(val period: Period) : SearchResultAction
    data class SetSort(val sort: Sort) : SearchResultAction
    data class TopicClick(val topicModel: TopicModel<out Topic>) : SearchResultAction
    object BackClick : SearchResultAction
    object ExpandAppBarClick : SearchResultAction
    object ListBottomReached : SearchResultAction
    object RetryClick : SearchResultAction
    object SearchClick : SearchResultAction
}
