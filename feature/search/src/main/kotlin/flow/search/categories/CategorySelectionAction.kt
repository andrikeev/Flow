package flow.search.categories

import flow.search.domain.models.ForumTreeItem

internal sealed interface CategorySelectionAction {
    object RetryClick : CategorySelectionAction
    data class ExpandClick(val item: ForumTreeItem) : CategorySelectionAction
    data class SelectClick(val item: ForumTreeItem) : CategorySelectionAction
}
