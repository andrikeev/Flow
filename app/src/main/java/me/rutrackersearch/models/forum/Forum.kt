package me.rutrackersearch.models.forum

data class Forum(
    val children: List<RootCategory>,
)

data class RootCategory(
    val name: String,
    val children: List<Category>,
)

sealed interface ForumItem {
    data class Category(val category: me.rutrackersearch.models.forum.Category) : ForumItem
    data class Topic(val topic: me.rutrackersearch.models.topic.Topic) : ForumItem
}
