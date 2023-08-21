package flow.forum

import flow.models.forum.ForumCategory

internal sealed interface ForumAction {
    data class CategoryClick(val category: ForumCategory) : ForumAction
    data class ExpandClick(val expandable: Expandable<ForumCategory>) : ForumAction
    data object RetryClick : ForumAction
}
