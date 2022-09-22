package me.rutrackersearch.app.ui.forum.bookmarks

import me.rutrackersearch.models.forum.CategoryModel

sealed interface BookmarksAction {
    data class BookmarkClicked(val bookmark: CategoryModel): BookmarksAction
}
