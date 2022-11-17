package flow.designsystem.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

object Border {
    val outline
        @Composable
        get() = BorderStroke(
            width = Dp.Hairline,
            color = MaterialTheme.colorScheme.outline
        )
    val outlineVariant
        @Composable
        get() = BorderStroke(
            width = Dp.Hairline,
            color = MaterialTheme.colorScheme.outlineVariant
        )
}
