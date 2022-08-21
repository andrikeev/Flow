package me.rutrackersearch.models.forum

import me.rutrackersearch.models.topic.Topic

data class Forum(
    val children: List<RootCategory>,
)

data class RootCategory(
    val name: String,
    val children: List<Category>,
)

sealed interface ForumItem
data class ForumCategory(val category: Category) : ForumItem
data class ForumTopic(val topic: Topic) : ForumItem
