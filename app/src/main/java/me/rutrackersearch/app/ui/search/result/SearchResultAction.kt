package me.rutrackersearch.app.ui.search.result

import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Order
import me.rutrackersearch.models.search.Period
import me.rutrackersearch.models.search.Sort
import me.rutrackersearch.models.topic.Author
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent

sealed interface SearchResultAction {
    object BackClick : SearchResultAction
    data class FavoriteClick(val torrent: TopicModel<Torrent>) : SearchResultAction
    object ListBottomReached : SearchResultAction
    object RetryClick : SearchResultAction
    object SearchClick : SearchResultAction
    data class SetAuthor(val author: Author?) : SearchResultAction
    data class SetCategories(val categories: List<Category>?) : SearchResultAction
    data class SetOrder(val order: Order) : SearchResultAction
    data class SetPeriod(val period: Period) : SearchResultAction
    data class SetSort(val sort: Sort) : SearchResultAction
    data class TorrentClick(val torrent: Torrent) : SearchResultAction
}
