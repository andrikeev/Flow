package me.rutrackersearch.app.ui.search.result

import me.rutrackersearch.app.ui.paging.LoadStates
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent

data class SearchResultState(
    val filter: Filter,
    val content: SearchResultContent = SearchResultContent.Initial,
    val loadStates: LoadStates = LoadStates.idle,
)

sealed interface SearchResultContent {
    object Initial : SearchResultContent
    object Empty : SearchResultContent
    data class Content(
        val torrents: List<TopicModel<Torrent>>,
        val categories: List<Category>,
        val page: Int,
        val pages: Int,
    ) : SearchResultContent
}
