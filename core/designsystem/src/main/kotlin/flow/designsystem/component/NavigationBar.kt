package flow.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import flow.designsystem.theme.Elevation

@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    tonalElevation: Dp = Elevation.small,
    content: @Composable RowScope.() -> Unit,
) = Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = tonalElevation,
) {
    androidx.compose.material3.NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = Color.Transparent,
        contentColor = contentColor,
        tonalElevation = 0.dp,
        content = content,
    )
}

@Composable
fun RowScope.NavigationBarItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) = NavigationBarItem(
    selected = selected,
    onClick = onClick,
    icon = { Icon(imageVector = icon, contentDescription = label) },
    modifier = modifier,
    enabled = true,
    label = { Text(label) },
    alwaysShowLabel = true,
)
