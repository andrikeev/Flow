package flow.forum.root

import flow.models.forum.Category
import flow.models.forum.RootCategory

internal sealed interface RootForumAction {
    data class CategoryClick(val category: Category) : RootForumAction
    data class ExpandClick(val expandable: Expandable<RootCategory>) : RootForumAction
    object RetryClick : RootForumAction
}
