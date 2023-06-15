package flow.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import flow.designsystem.drawables.Icon
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.contentColorFor

@Composable
@NonRestartableComposable
fun NavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = AppTheme.colors.surface,
    contentColor: Color = AppTheme.colors.contentColorFor(containerColor),
    tonalElevation: Dp = AppTheme.elevations.large,
    content: @Composable RowScope.() -> Unit,
) = Surface(
    modifier = modifier,
    color = AppTheme.colors.surface,
    tonalElevation = tonalElevation,
    shadowElevation = tonalElevation,
) {
    androidx.compose.material3.NavigationBar(
        modifier = Modifier.padding(WindowInsets.Companion.navigationBars.asPaddingValues()),
        containerColor = Color.Transparent,
        contentColor = contentColor,
        tonalElevation = AppTheme.elevations.zero,
        content = content,
    )
}

@Composable
@NonRestartableComposable
fun RowScope.NavigationBarItem(
    modifier: Modifier = Modifier,
    icon: Icon,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) = NavigationBarItem(
    selected = selected,
    onClick = onClick,
    icon = { Icon(icon = icon, contentDescription = label) },
    modifier = modifier,
    enabled = true,
    label = { Text(label) },
    alwaysShowLabel = true,
    colors = NavigationBarItemDefaults.colors(
        selectedIconColor = AppTheme.colors.onPrimaryContainer,
        selectedTextColor = AppTheme.colors.onPrimaryContainer,
        indicatorColor = AppTheme.colors.primaryContainer,
        unselectedIconColor = AppTheme.colors.onSurface,
        unselectedTextColor = AppTheme.colors.onSurface,
    ),
)
