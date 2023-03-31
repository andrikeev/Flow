package flow.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppSizes(
    val small: Dp = 16.dp,
    val mediumSmall: Dp = 24.dp,
    val medium: Dp = 32.dp,
    val default: Dp = 48.dp,
    val large: Dp = 56.dp,
    val extraLarge: Dp = 72.dp,
)

internal val LocaleSizes = staticCompositionLocalOf { AppSizes() }
