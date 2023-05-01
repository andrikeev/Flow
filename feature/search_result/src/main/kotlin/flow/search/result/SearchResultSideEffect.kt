package flow.search.result

import flow.models.search.Filter
import flow.models.topic.Torrent

internal sealed interface SearchResultSideEffect {
    object Back : SearchResultSideEffect
    data class OpenSearchInput(val filter: Filter) : SearchResultSideEffect
    data class OpenSearchResult(val filter: Filter) : SearchResultSideEffect
    data class OpenTopic(val id: String) : SearchResultSideEffect
}
