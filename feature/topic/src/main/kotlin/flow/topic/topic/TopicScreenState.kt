package flow.topic.topic

import flow.domain.model.LoadStates
import flow.models.auth.AuthState
import flow.models.topic.Post

internal data class TopicScreenState(
    val favoriteState: TopicFavoriteState = TopicFavoriteState.Initial,
    val paginationState: PaginationState = PaginationState.Initial,
    val topicContent: TopicContent = TopicContent.Initial,
    val loadStates: LoadStates = LoadStates.Idle,
)

internal sealed interface TopicFavoriteState {
    object Initial : TopicFavoriteState
    data class FavoriteState(val favorite: Boolean) : TopicFavoriteState
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

internal data class TopicState(val title: String)
