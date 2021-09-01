package me.rutrackersearch.app.ui.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Focusable(
    modifier: Modifier = Modifier,
    spec: FocusableSpec = focusableSpec(),
    content: @Composable (PaddingValues) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (isFocused) spec.scale else ContentScale.no
    )
    val elevation by animateDpAsState(
        if (isFocused) spec.elevation else ContentElevation.zero
    )
    val color = if (isFocused) {
        spec.color
    } else {
        Color.Unspecified
    }
    Surface(
        modifier = modifier
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused },
        shape = spec.shape,
        tonalElevation = elevation,
        color = color,
        content = { content(spec.padding) },
    )
}

@Composable
fun focusableSpec(
    scale: Float = ContentScale.no,
    elevation: Dp = ContentElevation.zero,
    shape: Shape = Shapes.None,
    color: Color = Color.Unspecified,
    paddingValues: PaddingValues = PaddingValues(0.dp),
): FocusableSpec = FocusableSpec(scale, elevation, shape, color, paddingValues)

@Stable
data class FocusableSpec(
    val scale: Float,
    val elevation: Dp,
    val shape: Shape,
    val color: Color,
    val padding: PaddingValues,
)

@Composable
fun rememberFocusRequester(): FocusRequester {
    return remember { FocusRequester() }
}
