package flow.models.forum

data class Forum(
    val children: List<ForumCategory>,
)

data class ForumCategory(
    val id: String,
    val name: String,
    val children: List<ForumCategory> = emptyList(),
)

sealed interface ForumItem {
    data class Category(val category: flow.models.forum.Category) : ForumItem
    data class Topic(val topic: flow.models.topic.Topic) : ForumItem
}
