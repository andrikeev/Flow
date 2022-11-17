package flow.search.history

import flow.models.search.Filter

internal sealed interface SearchSideEffect {
    object OpenLogin : SearchSideEffect
    object OpenSearchInput : SearchSideEffect
    data class OpenSearch(val filter: Filter) : SearchSideEffect
}
