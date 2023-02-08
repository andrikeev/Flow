package flow.forum

import flow.models.forum.Category

internal sealed interface ForumSideEffect {
    data class OpenCategory(val category: Category) : ForumSideEffect
}
