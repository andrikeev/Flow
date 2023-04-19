package flow.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppElevations(
    val zero: Dp = 0.dp,
    val small: Dp = 1.dp,
    val medium: Dp = 3.dp,
    val large: Dp = 6.dp,
)

internal val LocalElevations = staticCompositionLocalOf { AppElevations() }
