package flow.search

import flow.models.search.Search

internal sealed interface SearchAction {
    object LoginClick : SearchAction
    object SearchActionClick : SearchAction
    data class SearchItemClick(val search: Search) : SearchAction
}
