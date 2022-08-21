package me.rutrackersearch.app.ui.search.result

import me.rutrackersearch.app.ui.common.PageResult
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent

data class SearchResultState(
    val filter: Filter,
    val content: PageResult<List<TopicModel<Torrent>>> = PageResult.Loading(),
    val categories: List<Category> = emptyList(),
)
