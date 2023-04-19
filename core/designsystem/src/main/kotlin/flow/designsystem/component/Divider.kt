package flow.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme

@Composable
@NonRestartableComposable
fun Divider(
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.outlineVariant,
    startIndent: Dp = AppTheme.spaces.zero,
) = androidx.compose.material3.Divider(
    modifier = modifier.padding(start = startIndent),
    color = color,
    thickness = Dp.Hairline,
)

@ThemePreviews
@Composable
private fun DividerPreview() {
    FlowTheme {
        Surface {
            Divider(
                modifier = Modifier
                    .padding(AppTheme.spaces.large)
                    .fillMaxWidth()
            )
        }
    }
}
