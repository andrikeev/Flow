package flow.forum.bookmarks

internal sealed interface BookmarksSideEffect {
    data class OpenCategory(val categoryId: String) : BookmarksSideEffect
}
