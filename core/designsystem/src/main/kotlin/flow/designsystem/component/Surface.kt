package flow.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.contentColorFor
import kotlin.math.ln

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    shape: Shape = AppTheme.shapes.rectangle,
    color: Color = AppTheme.colors.surface,
    contentColor: Color = AppTheme.colors.contentColorFor(color),
    tonalElevation: Dp = AppTheme.elevations.zero,
    shadowElevation: Dp = AppTheme.elevations.zero,
    border: BorderStroke? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val totalElevation = LocalAbsoluteTonalElevation.current + tonalElevation
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalAbsoluteTonalElevation provides totalElevation,
    ) {
        Box(
            modifier = modifier.surface(
                color = color.atElevation(totalElevation),
                shape = shape,
                border = border,
                shadowElevation = shadowElevation,
            ),
            propagateMinConstraints = true,
            content = content,
        )
    }
}

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    shape: Shape = AppTheme.shapes.rectangle,
    color: Color = AppTheme.colors.surface,
    contentColor: Color = AppTheme.colors.contentColorFor(color),
    tonalElevation: Dp = AppTheme.elevations.zero,
    shadowElevation: Dp = AppTheme.elevations.zero,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable BoxScope.() -> Unit,
) {
    val totalElevation = LocalAbsoluteTonalElevation.current + tonalElevation
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalAbsoluteTonalElevation provides totalElevation,
    ) {
        Box(
            modifier = modifier
                .surface(
                    color = color.atElevation(tonalElevation),
                    shape = shape,
                    border = border,
                    shadowElevation = shadowElevation,
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(),
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick
                ),
            propagateMinConstraints = true,
            content = content
        )
    }
}

@Composable
fun InvertedSurface(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    shape: Shape = AppTheme.shapes.rectangle,
    color: Color = AppTheme.colors.surface,
    contentColor: Color = AppTheme.colors.contentColorFor(color),
    tonalElevation: Dp = AppTheme.elevations.zero,
    shadowElevation: Dp = AppTheme.elevations.zero,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable BoxScope.() -> Unit,
) {
    val localElevation = if (LocalAbsoluteTonalElevation.current == AppTheme.elevations.zero) {
        tonalElevation
    } else {
        AppTheme.elevations.zero
    }
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalAbsoluteTonalElevation provides localElevation,
    ) {
        Box(
            modifier = modifier
                .surface(
                    color = color.atElevation(localElevation),
                    shape = shape,
                    border = border,
                    shadowElevation = shadowElevation,
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(),
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick
                ),
            propagateMinConstraints = true,
            content = content
        )
    }
}


@Composable
private fun Color.atElevation(elevation: Dp): Color {
    return if (elevation == AppTheme.elevations.zero) {
        this
    } else {
        val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
        AppTheme.colors.primary.copy(alpha = alpha).compositeOver(this)
    }
}

private fun Modifier.surface(
    color: Color,
    shape: Shape,
    border: BorderStroke?,
    shadowElevation: Dp,
) = this
    .shadow(shadowElevation, shape, clip = false)
    .then(if (border != null) Modifier.border(border, shape) else Modifier)
    .background(color = color, shape = shape)
    .clip(shape)
