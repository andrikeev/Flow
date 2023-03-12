package flow.search.result.domain.models

internal sealed interface ForumTreeItem {
    val id: String
    val name: String

    data class Root(
        override val id: String,
        override val name: String,
        val expandable: Boolean,
        val expanded: Boolean,
    ) : ForumTreeItem

    data class Group(
        override val id: String,
        override val name: String,
        val expandable: Boolean,
        val expanded: Boolean,
        val selectState: SelectState
    ) : ForumTreeItem

    data class Category(
        override val id: String,
        override val name: String,
        val selectState: SelectState
    ) : ForumTreeItem
}

internal enum class SelectState {
    PartSelected,
    Selected,
    Unselected,
}
