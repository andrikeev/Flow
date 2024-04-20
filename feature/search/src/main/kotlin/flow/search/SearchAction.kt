package flow.search

import flow.models.search.Search

internal sealed interface SearchAction {
    data class DeleteItemClick(val search: Search) : SearchAction
    data class PinItemClick(val search: Search) : SearchAction
    data class SearchItemClick(val search: Search) : SearchAction
    data class UnpinItemClick(val search: Search) : SearchAction
    data object LoginClick : SearchAction
    data object SearchActionClick : SearchAction
}
