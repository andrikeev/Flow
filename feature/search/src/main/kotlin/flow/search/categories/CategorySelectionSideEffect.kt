package flow.search.categories

import flow.models.forum.Category

interface CategorySelectionSideEffect {
    data class OnSelect(val items: List<Category>) : CategorySelectionSideEffect
    data class OnRemove(val items: List<Category>) : CategorySelectionSideEffect
}
