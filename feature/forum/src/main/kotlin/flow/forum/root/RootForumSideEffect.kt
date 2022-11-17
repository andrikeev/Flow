package flow.forum.root

import flow.models.forum.Category

internal sealed interface RootForumSideEffect {
    data class OpenCategory(val category: Category) : RootForumSideEffect
}
