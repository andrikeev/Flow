package flow.search.result.filter

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.DropdownMenu
import flow.designsystem.component.DropdownMenuItem
import flow.designsystem.component.Icon
import flow.designsystem.component.rememberExpandState
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.R as DsR

@Composable
internal fun <T> FilterDropdownItem(
    label: String,
    items: List<T>,
    selected: T,
    itemLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    val dropdownState = rememberExpandState()
    var width by remember { mutableStateOf(0) }
    FilterBarItem(label = label) {
        FilterBarItemContent(
            modifier = Modifier.onSizeChanged { width = it.width },
            onClick = dropdownState::expand,
        ) {
            BodyLarge(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = AppTheme.spaces.large),
                text = itemLabel(selected),
            )
            Icon(
                modifier = Modifier.padding(AppTheme.spaces.medium),
                icon = FlowIcons.DropDownExpand,
                contentDescription = stringResource(DsR.string.designsystem_action_expand),
            )
            DropdownMenu(
                modifier = Modifier.width(with(LocalDensity.current) { width.toDp() }),
                expanded = dropdownState.expanded,
                onDismissRequest = dropdownState::collapse,
                shape = AppTheme.shapes.small,
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .padding(horizontal = AppTheme.spaces.large)
                            .fillMaxWidth()
                            .height(AppTheme.sizes.default),
                        item = item,
                        itemLabel = itemLabel,
                        onClick = {
                            onSelect(item)
                            dropdownState.collapse()
                        },
                    )
                }
            }
        }
    }
}
