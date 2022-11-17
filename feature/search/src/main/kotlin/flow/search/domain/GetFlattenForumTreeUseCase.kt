package flow.search.domain

import flow.dispatchers.api.Dispatchers
import flow.domain.usecase.GetForumTreeUseCase
import flow.models.forum.Category
import flow.search.domain.models.ForumTreeItem
import flow.search.domain.models.SelectState
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class GetFlattenForumTreeUseCase @Inject constructor(
    private val getForumTreeUseCase: GetForumTreeUseCase,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(
        expanded: Set<String>,
        selected: Set<String>,
    ): List<ForumTreeItem> {
        return withContext(dispatchers.default) {
            val forumTree = getForumTreeUseCase()
            mutableListOf<ForumTreeItem>().apply {
                forumTree.children.mapIndexed { index, root ->
                    val rootId = "c-$index"
                    val rootItem = ForumTreeItem.Root(
                        id = rootId,
                        name = root.name,
                        expandable = root.children.isNotEmpty(),
                        expanded = expanded.contains(rootId),
                    )
                    add(rootItem)
                    if (expanded.contains(rootId)) {
                        root.children.forEach { forum ->
                            val forumId = forum.category.id
                            val children = forum.children
                            val childrenIds = forum.children.map(Category::id)
                            val groupItem = ForumTreeItem.Group(
                                id = forumId,
                                name = forum.category.name,
                                expandable = children.isNotEmpty(),
                                expanded = expanded.contains(forumId),
                                selectState = when {
                                    selected.contains(forumId) -> SelectState.Selected
                                    childrenIds.any(selected::contains) -> SelectState.PartSelected
                                    else -> SelectState.Unselected
                                },
                            )
                            add(groupItem)
                            if (expanded.contains(forumId)) {
                                children.forEach { category ->
                                    add(
                                        ForumTreeItem.Category(
                                            id = category.id,
                                            name = category.name,
                                            selectState = if (selected.contains(category.id)) {
                                                SelectState.Selected
                                            } else {
                                                SelectState.Unselected
                                            }
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
