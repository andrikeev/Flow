package me.rutrackersearch.app.ui.forum.category

import me.rutrackersearch.app.ui.common.PageResult
import me.rutrackersearch.domain.entity.CategoryModel
import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.topic.Topic

data class CategoryState(
    val category: CategoryModel,
    val content: PageResult<CategoryContent> = PageResult.Loading(),
)

data class CategoryContent(
    val categories: List<CategoryModel> = emptyList(),
    val topics: List<TopicModel<Topic>> = emptyList(),
)
