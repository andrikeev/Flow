package flow.forum.category

import flow.models.forum.Category
import flow.models.forum.CategoryModel
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.ui.component.LoadStates

internal data class CategoryState(
    val categoryModelState: CategoryModelState,
    val content: CategoryContent = CategoryContent.Initial,
    val loadStates: LoadStates = LoadStates.idle,
)

internal sealed interface CategoryModelState {
    val category: Category

    data class Initial(override val category: Category) : CategoryModelState
    data class Loaded(val categoryModel: CategoryModel) : CategoryModelState {
        override val category: Category = categoryModel.category
    }
}

internal sealed interface CategoryContent {
    object Initial : CategoryContent
    object Empty : CategoryContent
    data class Content(
        val categories: List<CategoryModel>,
        val topics: List<TopicModel<out Topic>>,
        val page: Int,
        val pages: Int,
    ) : CategoryContent
}
