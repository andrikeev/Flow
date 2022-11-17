package flow.models.forum

import flow.models.topic.Topic
import flow.models.topic.TopicModel

data class CategoryPage(
    val categories: List<CategoryModel>,
    val topics: List<TopicModel<out Topic>>,
    val page: Int,
    val pages: Int,
)
