package flow.search.categories

import flow.search.domain.models.ForumTreeItem

internal sealed interface CategorySelectionState {
    object Loading : CategorySelectionState
    data class Success(val items: List<ForumTreeItem>) : CategorySelectionState
    data class Error(val exception: Throwable? = null) : CategorySelectionState
}
