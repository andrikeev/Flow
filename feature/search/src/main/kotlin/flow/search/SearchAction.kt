package flow.search

import flow.models.search.Search

internal sealed interface SearchAction {
    data object LoginClick : SearchAction
    data object SearchActionClick : SearchAction
    data class SearchItemClick(val search: Search) : SearchAction
}
