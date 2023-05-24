package flow.forum

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import flow.designsystem.component.Divider
import flow.designsystem.component.LazyList
import flow.designsystem.component.Surface
import flow.designsystem.theme.AppTheme
import flow.forum.ForumAction.CategoryClick
import flow.forum.ForumAction.ExpandClick
import flow.forum.ForumAction.RetryClick
import flow.models.forum.ForumCategory
import flow.navigation.viewModel
import flow.ui.component.CategoryListItem
import flow.ui.component.ExpandableCategoryListItem
import flow.ui.component.dividedItems
import flow.ui.component.errorItem
import flow.ui.component.loadingItem
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ForumScreen(
    openCategory: (String) -> Unit,
) {
    ForumScreen(
        viewModel = viewModel(),
        openCategory = openCategory,
    )
}

@Composable
private fun ForumScreen(
    viewModel: ForumViewModel,
    openCategory: (String) -> Unit,
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ForumSideEffect.OpenCategory -> openCategory(sideEffect.categoryId)
        }
    }
    val state by viewModel.collectAsState()
    ForumScreen(state, viewModel::perform)
}

@Composable
private fun ForumScreen(
    state: ForumState,
    onAction: (ForumAction) -> Unit,
) = LazyList(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(vertical = AppTheme.spaces.medium),
) {
    when (state) {
        is ForumState.Loading -> loadingItem()
        is ForumState.Error -> errorItem(onRetryClick = { onAction(RetryClick) })
        is ForumState.Loaded -> dividedItems(
            items = state.forum,
            key = { it.item.name },
        ) { item ->
            RootCategory(
                rootCategory = item.item,
                isExpanded = item.expanded,
                onCategoryClick = { category -> onAction(CategoryClick(category)) },
                onExpandClick = { onAction(ExpandClick(item)) }
            )
        }
    }
}

@Composable
private fun RootCategory(
    rootCategory: ForumCategory,
    isExpanded: Boolean,
    onCategoryClick: (ForumCategory) -> Unit,
    onExpandClick: () -> Unit,
) {
    ExpandableCategoryListItem(
        text = rootCategory.name,
        expanded = isExpanded,
        onExpand = onExpandClick,
    )
    AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
    ) {
        Surface(tonalElevation = AppTheme.elevations.small) {
            Column(modifier = Modifier.padding(vertical = AppTheme.spaces.medium)) {
                rootCategory.children.forEachIndexed { index, category ->
                    CategoryListItem(
                        text = category.name,
                        onClick = { onCategoryClick(category) },
                        contentPadding = PaddingValues(
                            start = AppTheme.spaces.extraLarge,
                            end = AppTheme.spaces.large,
                        ),
                        contentElevation = AppTheme.elevations.small,
                    )
                    if (index < rootCategory.children.lastIndex) {
                        Divider(startIndent = AppTheme.spaces.extraLarge)
                    }
                }
            }
        }
    }
}
