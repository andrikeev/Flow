package flow.search.result.categories

import flow.search.result.domain.models.ForumTreeItem

internal sealed interface CategorySelectionAction {
    data object RetryClick : CategorySelectionAction
    data class ExpandClick(val item: ForumTreeItem) : CategorySelectionAction
    data class SelectClick(val item: ForumTreeItem) : CategorySelectionAction
}
