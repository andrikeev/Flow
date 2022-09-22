package me.rutrackersearch.app.ui.forum.root

import me.rutrackersearch.models.forum.Category

sealed interface ForumSideEffect {
    data class OpenCategory(val category: Category) : ForumSideEffect
}
