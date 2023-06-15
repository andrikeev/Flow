package flow.ui.component

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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import flow.designsystem.component.Body
import flow.designsystem.component.CheckBox
import flow.designsystem.component.ExpandCollapseIcon
import flow.designsystem.component.Icon
import flow.designsystem.component.Surface
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme

@Composable
fun CategoryListItem(
    modifier: Modifier = Modifier,
    text: String,
    contentPadding: PaddingValues = PaddingValues(horizontal = AppTheme.spaces.large),
    contentElevation: Dp = AppTheme.elevations.zero,
    onClick: () -> Unit,
) = Surface(
    onClick = onClick,
    tonalElevation = contentElevation,
) {
    CategoryListItem(
        modifier = modifier,
        text = text,
        icons = {
            Box(
                modifier = Modifier.size(AppTheme.sizes.default),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon = FlowIcons.ChevronRight,
                    contentDescription = null,
                )
            }
        },
        contentPadding = contentPadding,
    )
}

@Composable
fun ExpandableCategoryListItem(
    modifier: Modifier = Modifier,
    text: String,
    expanded: Boolean,
    contentPadding: PaddingValues = PaddingValues(horizontal = AppTheme.spaces.large),
    onExpand: () -> Unit,
    expandedContent: @Composable () -> Unit,
) = Surface(
    modifier = Modifier.padding(
        horizontal = AppTheme.spaces.large,
        vertical = AppTheme.spaces.medium,
    ),
    onClick = onExpand,
    shape = AppTheme.shapes.large,
    tonalElevation = AppTheme.elevations.small,
) {
    Column {
        CategoryListItem(
            modifier = modifier,
            text = text,
            icons = { ExpandCollapseIcon(expanded = expanded) },
            contentPadding = contentPadding,
        )
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
            content = { expandedContent() },
        )
    }
}

@Composable
fun SelectableCategoryListItem(
    modifier: Modifier = Modifier,
    text: String,
    contentPadding: PaddingValues = PaddingValues(
        start = AppTheme.spaces.large,
        end = AppTheme.spaces.small,
    ),
    selected: ToggleableState,
    onSelect: () -> Unit,
) = Surface(
    onClick = onSelect,
) {
    CategoryListItem(
        modifier = modifier,
        text = text,
        icons = { CheckBox(selectState = selected) },
        contentPadding = contentPadding,
    )
}

@Composable
fun ExpandableSelectableCategoryListItem(
    modifier: Modifier = Modifier,
    text: String,
    contentPadding: PaddingValues = PaddingValues(
        start = AppTheme.spaces.large,
        end = AppTheme.spaces.small,
    ),
    expanded: Boolean,
    selected: ToggleableState,
    onExpand: () -> Unit,
    onSelect: () -> Unit,
) {
    val elevation by animateDpAsState(
        targetValue = if (expanded) {
            AppTheme.elevations.small
        } else {
            AppTheme.elevations.zero
        },
        label = "ExpandableSelectableCategoryListItem_Elevation"
    )
    Surface(
        onClick = onExpand,
        tonalElevation = elevation,
    ) {
        CategoryListItem(
            modifier = modifier,
            text = text,
            icons = {
                ExpandCollapseIcon(expanded = expanded)
                CheckBox(selectState = selected, onClick = onSelect)
            },
            contentPadding = contentPadding,
        )
    }
}

@Composable
private fun CategoryListItem(
    modifier: Modifier = Modifier,
    text: String,
    icons: @Composable RowScope.() -> Unit,
    contentPadding: PaddingValues,
) = Row(
    modifier = modifier
        .fillMaxWidth()
        .padding(contentPadding)
        .defaultMinSize(minHeight = AppTheme.sizes.default),
    verticalAlignment = Alignment.CenterVertically,
) {
    Body(
        modifier = Modifier
            .weight(1f)
            .padding(
                horizontal = AppTheme.spaces.medium,
                vertical = AppTheme.spaces.large,
            ),
        text = text,
    )
    icons()
}
