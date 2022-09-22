package me.rutrackersearch.models.forum

import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.TopicModel

data class CategoryPage(
    val categories: List<CategoryModel>,
    val topics: List<TopicModel<out Topic>>,
    val page: Int,
    val pages: Int,
)
