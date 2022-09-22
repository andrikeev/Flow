package me.rutrackersearch.app.ui.search.root

import me.rutrackersearch.models.search.Search

sealed interface SearchState {
    object Initial : SearchState
    object Unauthorised : SearchState
    object Empty : SearchState
    data class SearchList(val items: List<Search>) : SearchState
}
