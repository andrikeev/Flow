package flow.search.result.categories

import flow.search.result.domain.models.ForumTreeItem

internal sealed interface CategorySelectionState {
    object Loading : CategorySelectionState
    data class Success(val items: List<ForumTreeItem>) : CategorySelectionState
    data class Error(val exception: Throwable? = null) : CategorySelectionState
}
