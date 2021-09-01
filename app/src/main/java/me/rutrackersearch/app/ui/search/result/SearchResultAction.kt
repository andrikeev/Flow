package me.rutrackersearch.app.ui.search.result

import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.entity.search.Filter
import me.rutrackersearch.domain.entity.search.Order
import me.rutrackersearch.domain.entity.search.Period
import me.rutrackersearch.domain.entity.search.Sort
import me.rutrackersearch.domain.entity.topic.Author
import me.rutrackersearch.domain.entity.topic.Torrent

sealed interface SearchResultAction {
    object BackClick : SearchResultAction
    object ListBottomReached : SearchResultAction
    object RetryClick : SearchResultAction
    data class FavoriteClick(val torrent: TopicModel<Torrent>) : SearchResultAction
    data class SearchClick(val filter: Filter) : SearchResultAction
    data class SetAuthor(val value: Author?) : SearchResultAction
    data class SetCategories(val value: List<Category>?) : SearchResultAction
    data class SetOrder(val value: Order) : SearchResultAction
    data class SetPeriod(val value: Period) : SearchResultAction
    data class SetSort(val value: Sort) : SearchResultAction
    data class TorrentClick(val torrent: Torrent) : SearchResultAction
}
