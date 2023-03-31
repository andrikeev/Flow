package flow.designsystem.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.BadgedBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import flow.designsystem.theme.AppTheme

@Composable
@NonRestartableComposable
fun BadgeBox(
    showBadge: Boolean,
    text: String? = null,
    content: @Composable BoxScope.() -> Unit,
) = BadgedBox(
    badge = { if (showBadge) Badge(text) },
    content = content,
)

@Composable
@NonRestartableComposable
fun Badge(text: String? = null) {
    androidx.compose.material3.Badge(
        containerColor = AppTheme.colors.primary,
        contentColor = AppTheme.colors.onPrimary,
        content = text?.let { { Label(text) } },
    )
}
