package me.rutrackersearch.domain.entity

import me.rutrackersearch.domain.entity.forum.Category

data class CategoryModel(
    val data: Category,
    val isBookmark: Boolean = false,
    val newTopicsCount: Int = 0,
)
