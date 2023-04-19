package flow.search.result.filter

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.DropDownExpandCollapseIcon
import flow.designsystem.component.DropdownMenu
import flow.designsystem.component.rememberExpandState

@Composable
internal fun <T> FilterDropdownItem(
    label: String,
    items: List<T>,
    selected: T,
    itemLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    val dropdownState = rememberExpandState()
    FilterBarItem(
        label = label,
        onClick = dropdownState::expand,
    ) {
        BodyLarge(
            modifier = Modifier.weight(1f),
            text = itemLabel(selected),
        )
        DropDownExpandCollapseIcon(expanded = dropdownState.expanded)
        DropdownMenu(
            expanded = dropdownState.expanded,
            onDismissRequest = dropdownState::collapse,
            items = items,
            labelMapper = itemLabel,
            onSelect = onSelect,
        )
    }
}
