package me.rutrackersearch.app.ui.search.root

import me.rutrackersearch.models.search.Filter

sealed interface SearchSideEffect {
    object OpenLogin : SearchSideEffect
    object OpenSearchInput : SearchSideEffect
    data class OpenSearch(val filter: Filter) : SearchSideEffect
}
