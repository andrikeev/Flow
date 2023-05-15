package flow.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
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
    onClick: () -> Unit,
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
        onClick = onClick,
    )
}

@Composable
fun ExpandableCategoryListItem(
    modifier: Modifier = Modifier,
    text: String,
    expanded: Boolean,
    contentPadding: PaddingValues = PaddingValues(horizontal = AppTheme.spaces.large),
    onExpand: () -> Unit,
) {
    val elevation by animateDpAsState(
        targetValue = if (expanded) {
            AppTheme.elevations.small
        } else {
            AppTheme.elevations.zero
        }
    )
    CategoryListItem(
        modifier = modifier,
        text = text,
        icons = { ExpandCollapseIcon(expanded = expanded) },
        contentPadding = contentPadding,
        contentElevation = elevation,
        onClick = onExpand,
    )
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
) {
    CategoryListItem(
        modifier = modifier,
        text = text,
        icons = { CheckBox(selectState = selected) },
        contentPadding = contentPadding,
        onClick = onSelect,
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
        }
    )
    CategoryListItem(
        modifier = modifier,
        text = text,
        icons = {
            ExpandCollapseIcon(expanded = expanded)
            CheckBox(selectState = selected, onClick = onSelect)
        },
        contentPadding = contentPadding,
        contentElevation = elevation,
        onClick = onExpand,
    )
}

@Composable
private fun CategoryListItem(
    modifier: Modifier = Modifier,
    text: String,
    icons: @Composable RowScope.() -> Unit,
    contentPadding: PaddingValues,
    contentElevation: Dp = AppTheme.elevations.zero,
    onClick: () -> Unit,
) = Surface(
    modifier = modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = AppTheme.sizes.default),
    tonalElevation = contentElevation,
    shadowElevation = contentElevation,
    onClick = onClick,
) {
    Row(
        modifier = Modifier.padding(contentPadding),
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
}
