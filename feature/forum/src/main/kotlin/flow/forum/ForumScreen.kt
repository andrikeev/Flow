package flow.forum

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import flow.designsystem.component.Body
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Divider
import flow.designsystem.component.ExpandCollapseIcon
import flow.designsystem.component.LazyList
import flow.designsystem.component.Surface
import flow.designsystem.theme.AppTheme
import flow.forum.ForumAction.CategoryClick
import flow.forum.ForumAction.ExpandClick
import flow.forum.ForumAction.RetryClick
import flow.models.forum.ForumCategory
import flow.navigation.viewModel
import flow.ui.component.errorItem
import flow.ui.component.loadingItem
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ForumScreen(
    openCategory: (String) -> Unit,
) = ForumScreen(
    viewModel = viewModel(),
    openCategory = openCategory,
)

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
        is ForumState.Loaded -> items(
            items = state.forum,
            key = { it.item.id },
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
    val elevation by animateDpAsState(
        targetValue = if (isExpanded) {
            AppTheme.elevations.large
        } else {
            AppTheme.elevations.small
        },
        label = "RootCategory_Elevation",
    )
    Surface(
        modifier = Modifier.padding(
            horizontal = AppTheme.spaces.large,
            vertical = AppTheme.spaces.mediumSmall,
        ),
        onClick = onExpandClick,
        shape = AppTheme.shapes.large,
        tonalElevation = elevation,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.spaces.large)
                    .defaultMinSize(minHeight = AppTheme.sizes.default),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BodyLarge(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            horizontal = AppTheme.spaces.medium,
                            vertical = AppTheme.spaces.large,
                        ),
                    text = rootCategory.name,
                )
                ExpandCollapseIcon(expanded = isExpanded)
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
                content = {
                    Column {
                        rootCategory.children.forEachIndexed { index, category ->
                            Divider()
                            Surface(
                                modifier = Modifier.defaultMinSize(
                                    minHeight = AppTheme.sizes.default,
                                ),
                                onClick = { onCategoryClick(category) },
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = AppTheme.spaces.extraLarge)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    Body(
                                        modifier = Modifier.padding(
                                            horizontal = AppTheme.spaces.medium,
                                            vertical = AppTheme.spaces.large,
                                        ),
                                        text = category.name,
                                    )
                                }
                            }
                            if (index == rootCategory.children.lastIndex) {
                                Spacer(modifier = Modifier.height(AppTheme.spaces.large))
                            }
                        }
                    }
                },
            )
        }
    }
}
