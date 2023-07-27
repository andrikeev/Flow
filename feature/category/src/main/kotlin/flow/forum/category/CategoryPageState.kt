package flow.forum.category

import flow.domain.model.LoadStates
import flow.models.auth.AuthState
import flow.models.topic.TopicModel

internal data class CategoryPageState(
    val authState: AuthState = AuthState.Unauthorized,
    val categoryState: CategoryState = CategoryState.Initial,
    val categoryContent: CategoryContent = CategoryContent.Initial,
    val loadStates: LoadStates = LoadStates.Idle,
)

internal sealed interface CategoryState {
    object Initial : CategoryState
    data class Category(
        val name: String,
        val isBookmark: Boolean,
    ) : CategoryState
}

internal sealed interface CategoryContent {
    object Initial : CategoryContent
    object Empty : CategoryContent
    data class Content(val items: List<CategoryItem>) : CategoryContent
}

internal sealed interface CategoryItem {
    data class SectionHeader(val name: String) : CategoryItem
    data class Category(val category: flow.models.forum.Category) : CategoryItem
    data class Topic(val topic: TopicModel<out flow.models.topic.Topic>) : CategoryItem
}
