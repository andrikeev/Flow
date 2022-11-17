package flow.search.history

import flow.models.search.Search

internal sealed interface SearchAction {
    object SearchActionClick : SearchAction
    object LoginClick : SearchAction
    data class SearchItemClick(val search: Search) : SearchAction
}
