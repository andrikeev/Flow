package flow.forum

import flow.models.forum.ForumCategory

sealed interface ForumState {
    object Loading : ForumState
    data class Loaded(val forum: List<Expandable<ForumCategory>>) : ForumState
    data class Error(val error: Throwable) : ForumState
}

data class Expandable<T>(
    val item: T,
    val expanded: Boolean = false,
)
