package flow.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Divider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    thickness: Dp = Dp.Hairline,
    startIndent: Dp = 0.dp
) = androidx.compose.material3.Divider(
    modifier = modifier.padding(start = startIndent),
    color = color,
    thickness = thickness,
)
