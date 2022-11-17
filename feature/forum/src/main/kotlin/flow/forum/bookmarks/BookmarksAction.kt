package flow.forum.bookmarks

import flow.models.forum.CategoryModel

internal sealed interface BookmarksAction {
    data class BookmarkClicked(val bookmark: CategoryModel) : BookmarksAction
}
