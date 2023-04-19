package flow.designsystem.component

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme

@Composable
@NonRestartableComposable
fun <T> DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: Iterable<T>,
    labelMapper: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(
        x = AppTheme.spaces.zero,
        y = AppTheme.spaces.zero,
    ),
    onSelect: (T) -> Unit,
) = MaterialTheme(
    colorScheme = lightColorScheme(
        primary = AppTheme.colors.primary,
        onPrimary = AppTheme.colors.onPrimary,
        primaryContainer = AppTheme.colors.primaryContainer,
        onPrimaryContainer = AppTheme.colors.onPrimaryContainer,
        outline = AppTheme.colors.outline,
        outlineVariant = AppTheme.colors.outlineVariant,
        surface = AppTheme.colors.surface,
        onSurface = AppTheme.colors.onSurface,
        background = AppTheme.colors.background,
        onBackground = AppTheme.colors.onBackground,
    ),
) {
    androidx.compose.material3.DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        offset = offset,
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                onClick = {
                    onSelect(item)
                    onDismissRequest()
                },
                text = { Text(labelMapper.invoke(item)) },
            )
        }
    }
}

@Stable
class ExpandState(initialState: Boolean) {
    var expanded: Boolean by mutableStateOf(initialState)
        private set

    fun expand() {
        expanded = true
    }

    fun collapse() {
        expanded = false
    }

    fun toggle() {
        expanded = !expanded
    }
}

@Composable
fun rememberExpandState(initialState: Boolean = false) = remember { ExpandState(initialState) }

@ThemePreviews
@Composable
private fun DropdownMenuPreview() {
    FlowTheme {
        DropdownMenu(
            expanded = true,
            onDismissRequest = {},
            items = listOf("One", "Two", "Three", "Four", "Five"),
            labelMapper = { it },
            onSelect = {},
        )
    }
}
