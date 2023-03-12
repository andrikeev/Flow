package flow.forum.bookmarks

import flow.models.forum.Category

internal sealed interface BookmarksSideEffect {
    data class OpenCategory(val category: Category) : BookmarksSideEffect
}
