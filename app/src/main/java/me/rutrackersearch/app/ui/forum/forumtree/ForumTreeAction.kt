package me.rutrackersearch.app.ui.forum.forumtree

import me.rutrackersearch.domain.entity.forum.Category

sealed interface ForumTreeAction {
    object RetryClick : ForumTreeAction
    data class CategoryClick(val category: Category) : ForumTreeAction
    data class ExpandClick(val expandable: Expandable) : ForumTreeAction
}
