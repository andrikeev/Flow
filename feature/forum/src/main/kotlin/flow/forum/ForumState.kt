package flow.forum

import flow.models.forum.ForumCategory

sealed interface ForumState {
    sealed interface ForumLoadingState : ForumState
    data object Loading : ForumLoadingState
    data class Error(val error: Throwable) : ForumLoadingState
    data class Loaded(val forum: List<Expandable<ForumCategory>>) : ForumState
}

data class Expandable<T>(
    val item: T,
    val expanded: Boolean = false,
)
