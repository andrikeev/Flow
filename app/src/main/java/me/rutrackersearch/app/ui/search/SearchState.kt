package me.rutrackersearch.app.ui.search

import me.rutrackersearch.domain.entity.search.Search

sealed interface SearchState {
    object Initial : SearchState
    object Unauthorised : SearchState
    object Empty : SearchState
    data class SearchList(val items: List<Search>) : SearchState
}
