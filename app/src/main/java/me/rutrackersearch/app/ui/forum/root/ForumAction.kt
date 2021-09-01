package me.rutrackersearch.app.ui.forum.root

import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.entity.forum.RootCategory

sealed interface ForumAction {
    data class CategoryClick(val category: Category) : ForumAction
    data class ExpandClick(val expandable: Expandable<RootCategory>) : ForumAction
    object RetryClick : ForumAction
}
