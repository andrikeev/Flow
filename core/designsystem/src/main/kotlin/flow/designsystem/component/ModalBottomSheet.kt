package flow.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import flow.designsystem.theme.AppTheme

@Composable
fun ModalBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val transitionState = remember { MutableTransitionState(false) }
    transitionState.targetState = visible

    if (transitionState.currentState || transitionState.targetState) {
        val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues()

        Popup(onDismissRequest = onDismissRequest) {
            Box(
                modifier = Modifier
                    .padding(navigationBarsPadding)
                    .fillMaxSize(),
            ) {
                val transition = updateTransition(
                    transitionState = transitionState,
                    label = "DropdownMenu_Transition",
                )
                val scrimAlpha by transition.animateFloat(
                    targetValueByState = { if (it) 0.37f else 0.0f },
                    label = "Scrim_Alpha",
                )
                Scrim(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = scrimAlpha),
                    onDismissRequest = onDismissRequest,
                )
                transition.AnimatedVisibility(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    visible = { it },
                    enter = expandVertically(expandFrom = Alignment.Top),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top),
                ) {
                    Surface(
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
                            content = content,
                        )
                    }
                }
            }
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
