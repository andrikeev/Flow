package me.rutrackersearch.app.ui.search.result

import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.search.Order
import me.rutrackersearch.models.search.Period
import me.rutrackersearch.models.search.Sort
import me.rutrackersearch.models.topic.Author
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent

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
