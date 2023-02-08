package flow.forum

import flow.models.forum.RootCategory

sealed interface ForumState {
    object Loading : ForumState
    data class Loaded(val forum: List<Expandable<RootCategory>>) : ForumState
    data class Error(val error: Throwable) : ForumState
}

data class Expandable<T>(
    val item: T,
    val expanded: Boolean = false,
)
