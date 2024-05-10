package flow.designsystem.component

import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import flow.designsystem.theme.AppTheme
import flow.designsystem.utils.componentActivity
import flow.designsystem.utils.rememberSystemBarStyle

@Composable
fun ModalBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val transitionState = remember { MutableTransitionState(false) }
    transitionState.targetState = visible

    val activity = LocalContext.current.componentActivity
    val systemBarStyle = rememberSystemBarStyle()
    LaunchedEffect(visible, systemBarStyle) {
        activity.enableEdgeToEdge(
            statusBarStyle = if (visible) {
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            } else {
                systemBarStyle
            },
        )
    }

    if (transitionState.currentState || transitionState.targetState) {
        val transition = updateTransition(
            transitionState = transitionState,
            label = "ModalBottomSheet_Transition",
        )
        val scrimAlpha by transition.animateFloat(
            targetValueByState = { if (it) 0.37f else 0.0f },
            label = "Scrim_Alpha",
        )
        BackHandler(onBack = onDismissRequest)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(onDismissRequest) {
                    detectTapGestures {
                        onDismissRequest()
                    }
                }
                .background(Color.Black.copy(alpha = scrimAlpha)),
        ) {
            transition.AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = { it },
                enter = expandVertically(expandFrom = Alignment.Top),
                exit = shrinkVertically(shrinkTowards = Alignment.Top),
            ) {
                LazyColumn {
                    item {
                        Surface(
                            shape = ShapeDefaults.ExtraLarge.copy(
                                bottomStart = CornerSize(0.0.dp),
                                bottomEnd = CornerSize(0.0.dp),
                            ),
                            tonalElevation = AppTheme.elevations.large,
                            shadowElevation = AppTheme.elevations.large,

                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .imePadding()
                                    .navigationBarsPadding()
                                    .padding(
                                        top = AppTheme.spaces.extraLarge,
                                        bottom = AppTheme.spaces.large,
                                    ),
                                content = content,
                            )
                        }
                    }
                }
            }
        }
    }
}
