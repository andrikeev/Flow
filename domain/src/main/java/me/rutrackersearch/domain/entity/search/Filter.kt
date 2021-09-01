package me.rutrackersearch.domain.entity.search

import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.entity.topic.Author

data class Filter(
    val query: String? = null,
    val sort: Sort = Sort.DATE,
    val order: Order = Order.DESCENDING,
    val period: Period = Period.ALL_TIME,
    val author: Author? = null,
    val categories: List<Category>? = null,
)
