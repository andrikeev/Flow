package flow.models.forum

data class Forum(
    val children: List<ForumCategory>,
)

data class ForumCategory(
    val id: String,
    val name: String,
    val children: List<ForumCategory> = emptyList(),
)

data class ForumSection(
    val name: String,
    val topics: List<String>,
)

sealed interface ForumItem {
    data class Category(val category: flow.models.forum.Category) : ForumItem
    data class Section(val section: ForumSection) : ForumItem
    data class Topic(val topic: flow.models.topic.Topic) : ForumItem
}
