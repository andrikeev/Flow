package me.rutrackersearch.app.ui.forum.category

import me.rutrackersearch.app.ui.paging.LoadStates
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.forum.CategoryModel
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.TopicModel

data class CategoryState(
    val categoryModelState: CategoryModelState,
    val content: CategoryContent = CategoryContent.Initial,
    val loadStates: LoadStates = LoadStates.idle,
)

sealed interface CategoryModelState {
    data class Initial(val category: Category) : CategoryModelState
    data class Loaded(val categoryModel: CategoryModel) : CategoryModelState
}

sealed interface CategoryContent {
    object Initial : CategoryContent
    object Empty : CategoryContent
    data class Content(
        val categories: List<CategoryModel>,
        val topics: List<TopicModel<out Topic>>,
        val page: Int,
        val pages: Int,
    ) : CategoryContent
}

val CategoryModelState.category: Category
    get() = when (this) {
        is CategoryModelState.Initial -> category
        is CategoryModelState.Loaded -> categoryModel.category
    }
