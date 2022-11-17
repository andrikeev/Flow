package flow.search.categories

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.logger.api.LoggerFactory
import flow.models.forum.Category
import flow.search.domain.GetCategoriesByGroupIdUseCase
import flow.search.domain.GetFlattenForumTreeUseCase
import flow.search.domain.models.ForumTreeItem
import flow.search.categories.CategorySelectionAction.ExpandClick
import flow.search.categories.CategorySelectionAction.RetryClick
import flow.search.categories.CategorySelectionAction.SelectClick
import kotlinx.coroutines.coroutineScope
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class CategorySelectionViewModel @Inject constructor(
    private val getFlattenForumTreeUseCase: GetFlattenForumTreeUseCase,
    private val getCategoriesByGroupIdUseCase: GetCategoriesByGroupIdUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<CategorySelectionState, CategorySelectionSideEffect> {
    private val logger = loggerFactory.get("CategorySelectionViewModel")
    private val selectedIds = mutableSetOf<String>()
    private val expandedIds = mutableSetOf<String>()

    override val container: Container<CategorySelectionState, CategorySelectionSideEffect> =
        container(CategorySelectionState.Loading)

    fun setSelectedCategories(categories: Collection<Category>) {
        selectedIds.clear()
        selectedIds.addAll(categories.map(Category::id))
        loadForumTree()
    }

    fun perform(action: CategorySelectionAction) {
        when (action) {
            is ExpandClick -> onExpandClick(action.item)
            is RetryClick -> onRetry()
            is SelectClick -> onSelectClick(action.item)
        }
    }

    private fun onRetry() {
        intent { reduce { CategorySelectionState.Loading } }
        loadForumTree()
    }

    private fun loadForumTree() {
        intent {
            coroutineScope {
                runCatching { getFlattenForumTreeUseCase.invoke(expandedIds, selectedIds) }
                    .onSuccess { forumTreeItems ->
                        reduce { CategorySelectionState.Success(forumTreeItems) }
                    }
                    .onFailure { error ->
                        logger.e(error) { "loadForumTree" }
                        reduce { CategorySelectionState.Error(error) }
                    }
            }
        }
    }

    private fun onExpandClick(item: ForumTreeItem) {
        intent {
            coroutineScope {
                runCatching {
                    if (expandedIds.contains(item.id)) {
                        expandedIds.remove(item.id)
                    } else {
                        expandedIds.add(item.id)
                    }
                    val forumTreeItems = getFlattenForumTreeUseCase.invoke(expandedIds, selectedIds)
                    reduce { CategorySelectionState.Success(forumTreeItems) }
                }.onFailure { logger.e(it) { "onExpandClick($item)" } }
            }
        }
    }

    private fun onSelectClick(item: ForumTreeItem) {
        intent {
            coroutineScope {
                runCatching {
                    val id = item.id
                    val updatedCategories = when (item) {
                        is ForumTreeItem.Group -> getCategoriesByGroupIdUseCase.invoke(id)
                        is ForumTreeItem.Category -> listOf(Category(id, item.name))
                        is ForumTreeItem.Root -> emptyList()
                    }
                    val updatedIds = updatedCategories.map(Category::id).toSet()
                    if (selectedIds.contains(id)) {
                        selectedIds.removeAll(updatedIds)
                        postSideEffect(CategorySelectionSideEffect.OnRemove(updatedCategories))
                    } else {
                        selectedIds.addAll(updatedIds)
                        postSideEffect(CategorySelectionSideEffect.OnSelect(updatedCategories))
                    }
                    val forumTreeItems = getFlattenForumTreeUseCase.invoke(expandedIds, selectedIds)
                    reduce { CategorySelectionState.Success(forumTreeItems) }
                }
            }.onFailure { logger.e(it) { "onSelectClick($item)" } }
        }
    }
}
