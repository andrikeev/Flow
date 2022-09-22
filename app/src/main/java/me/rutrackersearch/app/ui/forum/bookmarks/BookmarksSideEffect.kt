package me.rutrackersearch.app.ui.forum.bookmarks

import me.rutrackersearch.models.forum.Category

sealed interface BookmarksSideEffect {
    data class OpenCategory(val category: Category): BookmarksSideEffect
}
