package flow.models.forum

data class Forum(
    val children: List<RootCategory>,
)

data class RootCategory(
    val name: String,
    val children: List<Category>,
)

sealed interface ForumItem {
    data class Category(val category: flow.models.forum.Category) : ForumItem
    data class Topic(val topic: flow.models.topic.Topic) : ForumItem
}
