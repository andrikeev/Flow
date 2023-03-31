package flow.topic.topic

import flow.domain.model.LoadStates
import flow.models.auth.AuthState
import flow.models.topic.Post

internal data class TopicPageState(
    val authState: AuthState = AuthState.Unauthorized,
    val topicState: TopicState = TopicState.Initial,
    val paginationState: PaginationState = PaginationState.Initial,
    val topicContent: TopicContent = TopicContent.Initial,
    val loadStates: LoadStates = LoadStates.Idle,
)

internal sealed interface TopicState {
    object Initial : TopicState
    data class Topic(
        val name: String,
        val isFavorite: Boolean,
    ) : TopicState
}

internal sealed interface PaginationState {
    object Initial : PaginationState
    data class Pagination(
        val page: Int,
        val pages: Int,
    ) : PaginationState
}

internal sealed interface TopicContent {
    object Initial : TopicContent
    object Empty : TopicContent
    data class Posts(val posts: List<Post>) : TopicContent
}
