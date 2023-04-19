package flow.designsystem.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import flow.designsystem.theme.AppTheme

@Composable
fun Focusable(
    modifier: Modifier = Modifier,
    spec: FocusableSpec = focusableSpec(),
    content: @Composable (PaddingValues) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(
        if (isFocused) spec.elevation else AppTheme.elevations.zero
    )
    val color = if (isFocused) {
        spec.color
    } else {
        Color.Unspecified
    }
    Surface(
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused },
        shape = spec.shape,
        tonalElevation = elevation,
        color = color,
        content = { content(spec.padding) },
    )
}

@Composable
fun focusableSpec(
    elevation: Dp = AppTheme.elevations.zero,
    shape: Shape = RectangleShape,
    color: Color = Color.Unspecified,
    paddingValues: PaddingValues = PaddingValues(AppTheme.spaces.zero),
): FocusableSpec = FocusableSpec(elevation, shape, color, paddingValues)

@Immutable
data class FocusableSpec(
    val elevation: Dp,
    val shape: Shape,
    val color: Color,
    val padding: PaddingValues,
)

@Composable
fun rememberFocusRequester(): FocusRequester {
    return remember { FocusRequester() }
}
