package flow.forum

import flow.models.forum.Category
import flow.models.forum.RootCategory

internal sealed interface ForumAction {
    data class CategoryClick(val category: Category) : ForumAction
    data class ExpandClick(val expandable: Expandable<RootCategory>) : ForumAction
    object RetryClick : ForumAction
}
