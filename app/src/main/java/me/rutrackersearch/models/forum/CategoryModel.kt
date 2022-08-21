package me.rutrackersearch.models.forum

data class CategoryModel(
    val data: Category,
    val isBookmark: Boolean = false,
    val newTopicsCount: Int = 0,
)
