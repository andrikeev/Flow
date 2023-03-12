package flow.forum.bookmarks

import flow.models.forum.CategoryModel

internal sealed interface BookmarksState {
    object Initial : BookmarksState
    object Empty : BookmarksState
    data class BookmarksList(val items: List<CategoryModel>) : BookmarksState
}
