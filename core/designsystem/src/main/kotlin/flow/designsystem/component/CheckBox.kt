package flow.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.state.ToggleableState
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme

@Composable
@NonRestartableComposable
fun CheckBox(
    selectState: ToggleableState,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) = Box(
    modifier = modifier.size(AppTheme.sizes.default),
    contentAlignment = Alignment.Center,
) {
    TriStateCheckbox(
        modifier = Modifier.scale(0.85f),
        state = selectState,
        onClick = onClick,
        colors = CheckboxDefaults.colors(
            checkedColor = AppTheme.colors.primary,
            checkmarkColor = AppTheme.colors.onPrimary,
            uncheckedColor = AppTheme.colors.outline,
            disabledCheckedColor = AppTheme.colors.outlineVariant,
            disabledUncheckedColor = AppTheme.colors.outlineVariant,
            disabledIndeterminateColor = AppTheme.colors.outlineVariant,
        ),
    )
}

@ThemePreviews
@Composable
private fun CheckBoxIconPreview() {
    FlowTheme {
        Surface {
            Column {
                CheckBox(selectState = ToggleableState.Off)
                CheckBox(selectState = ToggleableState.Indeterminate)
                CheckBox(selectState = ToggleableState.On)
            }
        }
    }
}
