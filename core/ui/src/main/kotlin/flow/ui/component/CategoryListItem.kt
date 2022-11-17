package flow.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import flow.designsystem.R
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.Elevation
import flow.designsystem.theme.FlowTheme

@Composable
fun CategoryListItem(
    modifier: Modifier = Modifier,
    text: String,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    onClick: () -> Unit,
) {
    CategoryListItem(
        modifier = modifier,
        text = text,
        icons = {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = FlowIcons.ArrowRight,
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
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    onExpand: () -> Unit,
) {
    val elevation by animateDpAsState(
        targetValue = if (expanded) {
            Elevation.small
        } else {
            Elevation.zero
        }
    )
    CategoryListItem(
        modifier = modifier,
        text = text,
        icons = { ExpandIcon(expanded = expanded) },
        contentPadding = contentPadding,
        contentElevation = elevation,
        onClick = onExpand,
    )
}

@Composable
fun SelectableCategoryListItem(
    modifier: Modifier = Modifier,
    text: String,
    contentPadding: PaddingValues = PaddingValues(start = 16.dp, end = 4.dp),
    selected: ToggleableState,
    onSelect: () -> Unit,
) {
    CategoryListItem(
        modifier = modifier,
        text = text,
        icons = { CheckBoxIcon(selectState = selected) },
        contentPadding = contentPadding,
        onClick = onSelect,
    )
}

@Composable
fun ExpandableSelectableCategoryListItem(
    modifier: Modifier = Modifier,
    text: String,
    contentPadding: PaddingValues = PaddingValues(start = 16.dp, end = 4.dp),
    expanded: Boolean,
    selected: ToggleableState,
    onExpand: () -> Unit,
    onSelect: () -> Unit,
) {
    val elevation by animateDpAsState(
        targetValue = if (expanded) {
            Elevation.small
        } else {
            Elevation.zero
        }
    )
    CategoryListItem(
        modifier = modifier,
        text = text,
        icons = {
            ExpandIcon(expanded = expanded)
            CheckBoxIcon(selectState = selected, onClick = onSelect)
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
    contentElevation: Dp = Elevation.zero,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp),
        tonalElevation = contentElevation,
        shadowElevation = contentElevation,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
            icons()
        }
    }
}

@Composable
fun ExpandIcon(
    modifier: Modifier = Modifier,
    expanded: Boolean,
) {
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
    Icon(
        modifier = modifier.rotate(rotation),
        imageVector = FlowIcons.Expand,
        contentDescription = stringResource(
            if (expanded) {
                R.string.designsystem_content_description_action_collapse
            } else {
                R.string.designsystem_content_description_action_expand
            }
        ),
    )
}

@Composable
fun CheckBoxIcon(
    modifier: Modifier = Modifier,
    selectState: ToggleableState,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier.size(48.dp),
        contentAlignment = Alignment.Center,
    ) {
        TriStateCheckbox(
            modifier = Modifier.scale(0.85f),
            state = selectState,
            onClick = onClick,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedColor = MaterialTheme.colorScheme.outline,
            ),
        )
    }
}

@Preview
@Composable
fun CheckBoxIcon_Preview() {
    FlowTheme {
        Surface {
            Column {
                CheckBoxIcon(selectState = ToggleableState.Off)
                CheckBoxIcon(selectState = ToggleableState.Indeterminate)
                CheckBoxIcon(selectState = ToggleableState.On)
            }
        }
    }
}