package flow.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme

@Composable
fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = AppTheme.shapes.small,
    content: @Composable ColumnScope.() -> Unit,
) {
    val transitionState = remember { MutableTransitionState(false) }
    transitionState.targetState = expanded

    if (transitionState.currentState || transitionState.targetState) {
        Popup(
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(
                focusable = true,
                clippingEnabled = false,
            ),
        ) {
            val transition = rememberTransition(
                transitionState = transitionState,
                label = "DropdownMenu_Transition"
            )
            val elevation by transition.animateDp(
                label = "DropdownMenu_Elevation",
                transitionSpec = {
                    if (false isTransitioningTo true) {
                        tween(
                            durationMillis = DropdownMenuDefaults.EnterAnimationDurationMillis,
                            easing = LinearOutSlowInEasing,
                        )
                    } else {
                        tween(
                            durationMillis = DropdownMenuDefaults.ExitAnimationDurationMillis,
                            delayMillis = DropdownMenuDefaults.ExitAnimationDelayMillis,
                        )
                    }
                },
                targetValueByState = {
                    if (it) {
                        AppTheme.elevations.large
                    } else {
                        AppTheme.elevations.zero
                    }
                },
            )
            transition.AnimatedVisibility(
                visible = { it },
                enter = expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(
                        durationMillis = DropdownMenuDefaults.EnterAnimationDurationMillis,
                        easing = LinearOutSlowInEasing,
                    ),
                    clip = false,
                ),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(
                        durationMillis = DropdownMenuDefaults.ExitAnimationDurationMillis,
                        delayMillis = DropdownMenuDefaults.ExitAnimationDelayMillis,
                    ),
                    clip = false,
                ),
            ) {
                Surface(
                    modifier = modifier,
                    shape = shape,
                    tonalElevation = elevation,
                    shadowElevation = elevation,
                    content = { Column(content = content) },
                )
            }
        }
    }
}

@Composable
fun <T> DropdownMenuItem(
    modifier: Modifier = Modifier,
    item: T,
    itemLabel: @Composable (T) -> String,
    onClick: () -> Unit,
) = Surface(
    onClick = onClick,
    content = {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.CenterStart,
        ) {
            BodyLarge(
                modifier = Modifier.align(Alignment.CenterStart),
                text = itemLabel(item),
            )
        }
    },
)

@Composable
@NonRestartableComposable
fun <T> DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: Iterable<T>,
    labelMapper: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(
        x = AppTheme.spaces.zero,
        y = AppTheme.spaces.zero,
    ),
    onSelect: (T) -> Unit,
) = MaterialTheme(
    colorScheme = lightColorScheme(
        primary = AppTheme.colors.primary,
        onPrimary = AppTheme.colors.onPrimary,
        primaryContainer = AppTheme.colors.primaryContainer,
        onPrimaryContainer = AppTheme.colors.onPrimaryContainer,
        outline = AppTheme.colors.outline,
        outlineVariant = AppTheme.colors.outlineVariant,
        surface = AppTheme.colors.surface,
        onSurface = AppTheme.colors.onSurface,
        background = AppTheme.colors.background,
        onBackground = AppTheme.colors.onBackground,
    ),
) {
    androidx.compose.material3.DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        offset = offset,
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                onClick = {
                    onSelect(item)
                    onDismissRequest()
                },
                text = { Text(labelMapper.invoke(item)) },
            )
        }
    }
}

private object DropdownMenuDefaults {
    const val EnterAnimationDurationMillis = 120
    const val ExitAnimationDurationMillis = 1
    const val ExitAnimationDelayMillis = 75
}

@Stable
class ExpandState(initialState: Boolean) {
    var expanded: Boolean by mutableStateOf(initialState)
        private set

    fun expand() {
        expanded = true
    }

    fun collapse() {
        expanded = false
    }

    fun toggle() {
        expanded = !expanded
    }
}

@Composable
fun rememberExpandState(initialState: Boolean = false) = remember { ExpandState(initialState) }

@ThemePreviews
@Composable
private fun DropdownMenuPreview() {
    FlowTheme {
        DropdownMenu(
            expanded = true,
            onDismissRequest = {},
            items = listOf("One", "Two", "Three", "Four", "Five"),
            labelMapper = { it },
            onSelect = {},
        )
    }
}
