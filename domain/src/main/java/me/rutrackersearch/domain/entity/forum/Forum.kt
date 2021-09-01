package me.rutrackersearch.domain.entity.forum

import me.rutrackersearch.domain.entity.topic.Topic

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
