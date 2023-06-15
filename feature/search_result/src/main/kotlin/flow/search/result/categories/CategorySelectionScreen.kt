package flow.search.result.categories

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import flow.designsystem.component.LazyList
import flow.designsystem.theme.AppTheme
import flow.designsystem.utils.RunOnFirstComposition
import flow.models.forum.Category
import flow.navigation.viewModel
import flow.search.result.categories.CategorySelectionAction.ExpandClick
import flow.search.result.categories.CategorySelectionAction.RetryClick
import flow.search.result.categories.CategorySelectionAction.SelectClick
import flow.search.result.domain.models.ForumTreeItem
import flow.search.result.domain.models.SelectState
import flow.ui.component.ExpandableCategoryListItem
import flow.ui.component.ExpandableSelectableCategoryListItem
import flow.ui.component.SelectableCategoryListItem
import flow.ui.component.dividedItems
import flow.ui.component.errorItem
import flow.ui.component.loadingItem
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun CategorySelectionScreen(
    selectedCategories: List<Category>,
    onCategoriesSelected: (List<Category>) -> Unit,
    onCategoriesRemoved: (List<Category>) -> Unit,
) {
    CategorySelectionScreen(
        viewModel = viewModel(),
        selectedCategories = selectedCategories,
        onCategoriesSelected = onCategoriesSelected,
        onCategoriesRemoved = onCategoriesRemoved,
    )
}

@Composable
private fun CategorySelectionScreen(
    viewModel: CategorySelectionViewModel,
    selectedCategories: List<Category>,
    onCategoriesSelected: (List<Category>) -> Unit,
    onCategoriesRemoved: (List<Category>) -> Unit,
) {
    RunOnFirstComposition { viewModel.setSelectedCategories(selectedCategories) }
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is CategorySelectionSideEffect.OnSelect -> onCategoriesSelected(sideEffect.items)
            is CategorySelectionSideEffect.OnRemove -> onCategoriesRemoved(sideEffect.items)
        }
    }
    val state by viewModel.collectAsState()
    CategorySelectionScreen(
        state = state,
        onAction = viewModel::perform,
    )
}

@Composable
private fun CategorySelectionScreen(
    state: CategorySelectionState,
    onAction: (CategorySelectionAction) -> Unit,
) = LazyList(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(vertical = AppTheme.spaces.medium),
) {
    when (state) {
        is CategorySelectionState.Loading -> loadingItem()
        is CategorySelectionState.Error -> errorItem(onRetryClick = { onAction(RetryClick) })
        is CategorySelectionState.Success -> dividedItems(
            items = state.items,
            key = ForumTreeItem::id,
            contentType = { it::class },
        ) { item ->
            ForumTreeItem(
                item = item,
                onExpandClick = { onAction(ExpandClick(item)) },
                onSelectClick = { onAction(SelectClick(item)) },
            )
        }
    }
}

@Composable
private fun ForumTreeItem(
    modifier: Modifier = Modifier,
    item: ForumTreeItem,
    onExpandClick: () -> Unit,
    onSelectClick: () -> Unit,
) {
    when (item) {
        is ForumTreeItem.Root -> {
            if (item.expandable) {
                ExpandableCategoryListItem(
                    modifier = modifier,
                    text = item.name,
                    expanded = item.expanded,
                    onExpand = onExpandClick,
                ) {}
            }
        }

        is ForumTreeItem.Group -> {
            if (item.expandable) {
                ExpandableSelectableCategoryListItem(
                    modifier = modifier,
                    text = item.name,
                    contentPadding = PaddingValues(
                        start = AppTheme.spaces.extraExtraLarge,
                        end = AppTheme.spaces.small,
                    ),
                    expanded = item.expanded,
                    selected = item.selectState.toUiState(),
                    onExpand = onExpandClick,
                    onSelect = onSelectClick,
                )
            } else {
                SelectableCategoryListItem(
                    modifier = modifier,
                    text = item.name,
                    contentPadding = PaddingValues(
                        start = AppTheme.spaces.extraExtraLarge,
                        end = AppTheme.spaces.small,
                    ),
                    selected = item.selectState.toUiState(),
                    onSelect = onSelectClick,
                )
            }
        }

        is ForumTreeItem.Category -> {
            SelectableCategoryListItem(
                modifier = modifier,
                text = item.name,
                contentPadding = PaddingValues(
                    start = AppTheme.spaces.extraExtraLarge,
                    end = AppTheme.spaces.small,
                ),
                selected = item.selectState.toUiState(),
                onSelect = onSelectClick,
            )
        }
    }
}

private fun SelectState.toUiState(): ToggleableState = when (this) {
    SelectState.PartSelected -> ToggleableState.Indeterminate
    SelectState.Selected -> ToggleableState.On
    SelectState.Unselected -> ToggleableState.Off
}
