package me.rutrackersearch.app.ui.search.result

import me.rutrackersearch.app.ui.common.PageResult
import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.entity.search.Filter
import me.rutrackersearch.domain.entity.topic.Torrent

data class SearchResultState(
    val filter: Filter,
    val content: PageResult<List<TopicModel<Torrent>>> = PageResult.Loading(),
    val categories: List<Category> = emptyList(),
)
