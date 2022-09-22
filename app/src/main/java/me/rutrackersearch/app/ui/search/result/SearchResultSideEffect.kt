package me.rutrackersearch.app.ui.search.result

import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.topic.Torrent

sealed interface SearchResultSideEffect {
    object Back : SearchResultSideEffect
    data class OpenSearchInput(val filter: Filter) : SearchResultSideEffect
    data class OpenSearchResult(val filter: Filter) : SearchResultSideEffect
    data class OpenTorrent(val torrent: Torrent) : SearchResultSideEffect
}
