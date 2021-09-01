package me.rutrackersearch.app.ui.forum.forumtree

sealed interface ForumTreeState {
    object Loading : ForumTreeState
    data class Loaded(val forum: List<ForumTreeItem>) : ForumTreeState
    data class Error(val error: Throwable) : ForumTreeState
}

sealed interface ForumTreeItem {
    val id: String
    val name: String
}

sealed interface Expandable : ForumTreeItem {
    val expanded: Boolean
}

data class ExpandableForumTreeRootGroup(
    override val id: String,
    override val name: String,
    override val expanded: Boolean,
) : Expandable

data class ExpandableForumTreeGroup(
    override val id: String,
    override val name: String,
    val expandable: Boolean,
    override val expanded: Boolean,
) : Expandable

data class ForumTreeCategory(
    override val id: String,
    override val name: String
) : ForumTreeItem
