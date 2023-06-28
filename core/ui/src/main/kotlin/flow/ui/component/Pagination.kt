package flow.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import flow.designsystem.component.IconButton
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.component.ThemePreviews
import flow.designsystem.component.rememberExpandState
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.ui.R

@Composable
fun Pagination(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit,
) = Column(modifier = Modifier.fillMaxWidth()) {
    val expandedState = rememberExpandState()
    AnimatedVisibility(
        visible = expandedState.expanded,
        enter = fadeIn() + expandVertically(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Adaptive(AppTheme.sizes.default),
            contentPadding = PaddingValues(AppTheme.spaces.small),
            horizontalArrangement = Arrangement.Center,
        ) {
            items(totalPages) {
                val page = it + 1
                val isSelected = currentPage == page
                Surface(
                    modifier = Modifier
                        .size(AppTheme.sizes.default)
                        .padding(AppTheme.spaces.small),
                    shape = AppTheme.shapes.small,
                    onClick = { onPageSelected(page) },
                    enabled = !isSelected,
                    color = if (isSelected) {
                        AppTheme.colors.primary
                    } else {
                        AppTheme.colors.surface
                    }
                ) {
                    Box(
                        modifier = Modifier.matchParentSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = page.toString(),
                        )
                    }
                }
            }
        }
    }
    Row(
        modifier = Modifier
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .fillMaxWidth()
            .height(AppTheme.sizes.default),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            icon = FlowIcons.FirstPage,
            contentDescription = stringResource(R.string.pagination_action_first_page),
            enabled = currentPage > 1,
            onClick = { onPageSelected(1) },
        )
        IconButton(
            icon = FlowIcons.PrevPage,
            contentDescription = stringResource(R.string.pagination_action_previous_page),
            enabled = currentPage > 1,
            onClick = { onPageSelected(currentPage - 1) },
        )
        TextButton(
            text = buildString {
                append(currentPage)
                if (totalPages > 0) {
                    append('/', totalPages)
                }
            },
            onClick = expandedState::toggle,
        )
        IconButton(
            icon = FlowIcons.NextPage,
            contentDescription = stringResource(R.string.pagination_action_next_page),
            enabled = currentPage < totalPages,
            onClick = { onPageSelected(currentPage + 1) },
        )
        IconButton(
            icon = FlowIcons.LastPage,
            contentDescription = stringResource(R.string.pagination_action_last_page),
            enabled = currentPage < totalPages,
            onClick = { onPageSelected(totalPages) },
        )
    }
}

@ThemePreviews
@Composable
private fun Pagination_Preview() {
    FlowTheme {
        Surface {
            var page by remember { mutableStateOf(2) }
            Pagination(page, 50) { page = it }
        }
    }
}
