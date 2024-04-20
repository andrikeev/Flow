package flow.search

import flow.models.search.Search

internal sealed interface SearchState {
    data object Initial : SearchState
    data object Unauthorised : SearchState

    sealed interface AuthorisedSearchState : SearchState
    data object Empty : AuthorisedSearchState
    data class SearchList(
        val pinned: List<Search>,
        val other: List<Search>,
    ) : AuthorisedSearchState
}

internal val SearchState.showSearchAction: Boolean
    get() = this is SearchState.AuthorisedSearchState
