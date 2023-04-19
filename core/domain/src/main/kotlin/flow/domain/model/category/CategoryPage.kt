package flow.domain.model.category

import flow.models.forum.Category
import flow.models.topic.Topic
import flow.models.topic.TopicModel

data class CategoryPage(
    val categories: List<Category> = emptyList(),
    val topics: List<TopicModel<out Topic>> = emptyList(),
)

fun CategoryPage.isEmpty() = categories.isEmpty() && topics.isEmpty()
