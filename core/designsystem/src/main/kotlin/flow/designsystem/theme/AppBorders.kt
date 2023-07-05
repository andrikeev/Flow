package flow.designsystem.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp

@Immutable
class AppBorders {
    val outline
        @Composable
        get() = BorderStroke(
            width = Dp.Hairline,
            color = AppTheme.colors.outline,
        )
}

internal val LocalBorders = staticCompositionLocalOf { AppBorders() }
