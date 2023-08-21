package flow.search

import flow.models.search.Filter

internal sealed interface SearchSideEffect {
    data object OpenLogin : SearchSideEffect
    data object OpenSearchInput : SearchSideEffect
    data class OpenSearch(val filter: Filter) : SearchSideEffect
}
