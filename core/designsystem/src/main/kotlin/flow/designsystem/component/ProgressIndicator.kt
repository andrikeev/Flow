package flow.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import flow.designsystem.theme.AppTheme

@Composable
@NonRestartableComposable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.primary,
    strokeWidth: Dp = 2.dp,
) = androidx.compose.material3.CircularProgressIndicator(
    modifier = modifier,
    color = color,
    strokeWidth = strokeWidth,
)
