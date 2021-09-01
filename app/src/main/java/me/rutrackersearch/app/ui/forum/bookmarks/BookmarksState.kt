package me.rutrackersearch.app.ui.forum.bookmarks

import me.rutrackersearch.domain.entity.CategoryModel

sealed interface BookmarksState {
    object Initial : BookmarksState
    object Empty : BookmarksState
    data class BookmarksList(val items: List<CategoryModel>) : BookmarksState
}
