package me.rutrackersearch.models.forum

data class ForumTree(
    val children: List<ForumTreeRootGroup>,
)

data class ForumTreeRootGroup(
    val name: String,
    val children: List<ForumTreeGroup>,
)

data class ForumTreeGroup(
    val category: Category,
    val children: List<Category>,
)
