package flow.forum.root

import flow.models.forum.RootCategory

sealed interface RootForumState {
    object Loading : RootForumState
    data class Loaded(val forum: List<Expandable<RootCategory>>) : RootForumState
    data class Error(val error: Throwable) : RootForumState
}

data class Expandable<T>(
    val item: T,
    val expanded: Boolean = false,
)
