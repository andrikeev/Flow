package me.rutrackersearch.app.ui.search.root

import me.rutrackersearch.models.search.Search

sealed interface SearchAction {
    object SearchActionClick : SearchAction
    object LoginClick : SearchAction
    data class SearchItemClick(val search: Search) : SearchAction
}
