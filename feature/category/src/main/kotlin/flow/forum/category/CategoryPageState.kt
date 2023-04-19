package flow.forum.category

import flow.domain.model.LoadStates
import flow.models.auth.AuthState
import flow.models.forum.Category
import flow.models.topic.Topic
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
    data class Content(
        val categories: List<Category>,
        val topics: List<TopicModel<out Topic>>,
    ) : CategoryContent
}
