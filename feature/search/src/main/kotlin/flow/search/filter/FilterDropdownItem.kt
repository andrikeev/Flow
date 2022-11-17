package flow.search.filter

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.Border

@Composable
internal fun <T> FilterDropdownItem(
    label: String,
    items: List<T>,
    selected: T,
    itemLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f), text = label
        )
        var expanded by remember { mutableStateOf(false) }
        Surface(
            modifier = Modifier.weight(2f),
            shape = MaterialTheme.shapes.small,
            border = Border.outline,
            onClick = { expanded = true },
        ) {
            Row(modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = itemLabel(selected),
                )
                Icon(
                    imageVector = if (expanded) {
                        FlowIcons.DropDownCollapse
                    } else {
                        FlowIcons.DropDownExpand
                    },
                    contentDescription = null,
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                onSelect(item)
                                expanded = false
                            },
                            text = { Text(itemLabel(item)) },
                        )
                    }
                }
            }
        }
    }
}
