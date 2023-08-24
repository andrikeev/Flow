package flow.forum.bookmarks

import flow.models.forum.CategoryModel

internal sealed interface BookmarksState {
    data object Initial : BookmarksState
    data object Empty : BookmarksState
    data class BookmarksList(val items: List<CategoryModel>) : BookmarksState
}
