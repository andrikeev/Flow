package flow.search

import flow.models.search.Search

internal sealed interface SearchState {
    object Initial : SearchState
    object Unauthorised : SearchState

    sealed interface AuthorisedSearchState : SearchState
    object Empty : AuthorisedSearchState
    data class SearchList(val items: List<Search>) : AuthorisedSearchState
}

internal val SearchState.showSearchAction: Boolean
    get() = this is SearchState.AuthorisedSearchState
