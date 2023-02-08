package flow.search

import flow.models.search.Search

internal sealed interface SearchState {
    object Initial : SearchState
    object Unauthorised : SearchState
    object Empty : SearchState
    data class SearchList(val items: List<Search>) : SearchState
}
