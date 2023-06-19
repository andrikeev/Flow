package flow.designsystem.component

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import flow.designsystem.theme.AppTheme
import flow.designsystem.utils.RunOnFirstComposition

@Composable
fun ModalBottomSheet(
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) = Popup(
    onDismissRequest = onDismissRequest,
    properties = PopupProperties(),
) {
    val state = rememberVisibilityState()
    val transition = updateTransition(
        targetState = state.visible,
        label = "Popup_Transition",
    )
    RunOnFirstComposition { state.show() }
    Box(modifier = Modifier.fillMaxSize()) {
        val alpha by transition.animateFloat(
            targetValueByState = { if (it) 0.37f else 0.0f },
            label = "Scrim_Alpha",
        )
        Scrim(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = alpha),
            onDismissRequest = onDismissRequest,
        )
        val verticalOffset by transition.animateDp(
            targetValueByState = { if (it) 0.dp else 100.dp },
            label = "BottomSheet_Offset",
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .absoluteOffset(y = verticalOffset),
            shape = ShapeDefaults.ExtraLarge.copy(
                bottomStart = CornerSize(0.0.dp),
                bottomEnd = CornerSize(0.0.dp),
            ),
            tonalElevation = AppTheme.elevations.large,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 28.0.dp, // ShapeDefaults.ExtraLarge
                        bottom = AppTheme.spaces.medium,
                    ),
                content = { content() },
            )
        }
    }
}

@Composable
private fun Scrim(
    color: Color,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
) = Canvas(
    modifier = modifier
        .pointerInput(onDismissRequest) {
            detectTapGestures {
                onDismissRequest()
            }
        },
    onDraw = { drawRect(color = color) },
)
