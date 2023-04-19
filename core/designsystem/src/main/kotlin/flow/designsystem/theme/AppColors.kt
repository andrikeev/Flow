package flow.designsystem.theme

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.color.MaterialColors
import flow.designsystem.component.Label
import flow.designsystem.theme.Accents.Companion.darkAccents
import flow.designsystem.theme.Accents.Companion.lightAccents

@Stable
class AppColors internal constructor(
    primary: Color,
    onPrimary: Color,
    primaryContainer: Color,
    onPrimaryContainer: Color,
    outline: Color,
    outlineVariant: Color,
    surface: Color,
    onSurface: Color,
    background: Color,
    onBackground: Color,
    error: Color,
    accentGreen: Color,
    accentBlue: Color,
    accentOrange: Color,
    accentRed: Color,
    val isDark: Boolean,
) {
    var primary by mutableStateOf(primary, structuralEqualityPolicy())
        internal set
    var onPrimary by mutableStateOf(onPrimary, structuralEqualityPolicy())
        internal set
    var primaryContainer by mutableStateOf(primaryContainer, structuralEqualityPolicy())
        internal set
    var onPrimaryContainer by mutableStateOf(onPrimaryContainer, structuralEqualityPolicy())
        internal set
    var outline by mutableStateOf(outline, structuralEqualityPolicy())
        internal set
    var outlineVariant by mutableStateOf(outlineVariant, structuralEqualityPolicy())
        internal set
    var surface by mutableStateOf(surface, structuralEqualityPolicy())
        internal set
    var onSurface by mutableStateOf(onSurface, structuralEqualityPolicy())
        internal set
    var background by mutableStateOf(background, structuralEqualityPolicy())
        internal set
    var onBackground by mutableStateOf(onBackground, structuralEqualityPolicy())
        internal set
    var error by mutableStateOf(error, structuralEqualityPolicy())
        internal set
    var accentBlue by mutableStateOf(accentBlue, structuralEqualityPolicy())
        internal set
    var accentGreen by mutableStateOf(accentGreen, structuralEqualityPolicy())
        internal set
    var accentOrange by mutableStateOf(accentOrange, structuralEqualityPolicy())
        internal set
    var accentRed by mutableStateOf(accentRed, structuralEqualityPolicy())
        internal set

    fun copy(
        primary: Color = this.primary,
        onPrimary: Color = this.onPrimary,
        primaryContainer: Color = this.primaryContainer,
        onPrimaryContainer: Color = this.onPrimaryContainer,
        outline: Color = this.outline,
        outlineVariant: Color = this.outlineVariant,
        background: Color = this.background,
        onBackground: Color = this.onBackground,
        surface: Color = this.surface,
        onSurface: Color = this.onSurface,
        error: Color = this.error,
        accentBlue: Color = this.accentBlue,
        accentGreen: Color = this.accentGreen,
        accentOrange: Color = this.accentOrange,
        accentRed: Color = this.accentRed,
        isDark: Boolean = this.isDark,
    ): AppColors =
        AppColors(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            background = background,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface,
            outline = outline,
            outlineVariant = outlineVariant,
            error = error,
            accentGreen = accentGreen,
            accentBlue = accentBlue,
            accentOrange = accentOrange,
            accentRed = accentRed,
            isDark = isDark,
        )
}

fun AppColors.contentColorFor(containerColor: Color) = when (containerColor) {
    primaryContainer -> onPrimaryContainer
    background -> onBackground
    surface -> onSurface
    primary -> onPrimary
    accentBlue -> onPrimary
    accentGreen -> onPrimary
    accentOrange -> onPrimary
    accentRed -> onPrimary
    else -> onBackground
}

internal fun lightColors(
    primary: Color = Indigo40,
    onPrimary: Color = Indigo100,
    primaryContainer: Color = Indigo90,
    onPrimaryContainer: Color = Indigo10,
    outline: Color = MidGray50,
    outlineVariant: Color = MidGray80,
    background: Color = SanMarino99,
    onBackground: Color = MidGray10,
    surface: Color = SanMarino99,
    onSurface: Color = MidGray10,
    error: Color = Thunderbird40,
    accentBlue: Color = lightAccents.accentBlue,
    accentGreen: Color = lightAccents.accentGreen,
    accentOrange: Color = lightAccents.accentOrange,
    accentRed: Color = lightAccents.accentRed,
) = AppColors(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    outline = outline,
    outlineVariant = outlineVariant,
    surface = surface,
    onSurface = onSurface,
    background = background,
    onBackground = onBackground,
    error = error,
    accentBlue = accentBlue,
    accentGreen = accentGreen,
    accentOrange = accentOrange,
    accentRed = accentRed,
    isDark = false,
)

internal fun darkColors(
    primary: Color = Indigo80,
    onPrimary: Color = Indigo20,
    primaryContainer: Color = Indigo30,
    onPrimaryContainer: Color = Indigo90,
    outline: Color = MidGray60,
    outlineVariant: Color = MidGray30,
    background: Color = MidGray10,
    onBackground: Color = MidGray90,
    surface: Color = MidGray10,
    onSurface: Color = MidGray90,
    error: Color = Thunderbird80,
    accentBlue: Color = darkAccents.accentBlue,
    accentGreen: Color = darkAccents.accentGreen,
    accentOrange: Color = darkAccents.accentOrange,
    accentRed: Color = darkAccents.accentRed,
) = AppColors(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    outline = outline,
    outlineVariant = outlineVariant,
    surface = surface,
    onSurface = onSurface,
    background = background,
    onBackground = onBackground,
    error = error,
    accentBlue = accentBlue,
    accentGreen = accentGreen,
    accentOrange = accentOrange,
    accentRed = accentRed,
    isDark = true,
)

@RequiresApi(Build.VERSION_CODES.S)
internal fun dynamicColors(context: Context, isDark: Boolean): AppColors {
    val dynamicColorScheme = if (isDark) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }
    val accents = if (isDark) {
        darkAccents
    } else {
        lightAccents
    }
    return AppColors(
        primary = dynamicColorScheme.primary,
        onPrimary = dynamicColorScheme.onPrimary,
        primaryContainer = dynamicColorScheme.primaryContainer,
        onPrimaryContainer = dynamicColorScheme.onPrimaryContainer,
        outline = dynamicColorScheme.outline,
        outlineVariant = dynamicColorScheme.outlineVariant,
        surface = dynamicColorScheme.surface,
        onSurface = dynamicColorScheme.onSurface,
        background = dynamicColorScheme.background,
        onBackground = dynamicColorScheme.onBackground,
        error = dynamicColorScheme.error.harmonize(dynamicColorScheme.primary, !isDark),
        accentBlue = accents.accentBlue.harmonize(dynamicColorScheme.primary, !isDark),
        accentGreen = accents.accentGreen.harmonize(dynamicColorScheme.primary, !isDark),
        accentOrange = accents.accentOrange.harmonize(dynamicColorScheme.primary, !isDark),
        accentRed = accents.accentRed.harmonize(dynamicColorScheme.primary, !isDark),
        isDark = isDark,
    )
}

private fun Color.harmonize(primary: Color, isLight: Boolean): Color {
    val accentColor = MaterialColors.harmonize(toArgb(), primary.toArgb())
    val accentRoles = MaterialColors.getColorRoles(accentColor, isLight)
    return Color(accentRoles.accent)
}

private data class Accents(
    val accentBlue: Color,
    val accentGreen: Color,
    val accentOrange: Color,
    val accentRed: Color,
) {
    companion object {
        val lightAccents = Accents(
            accentBlue = Denim40,
            accentGreen = FunGreen40,
            accentOrange = Tabasco40,
            accentRed = Monza40,
        )
        val darkAccents = Accents(
            accentBlue = Denim80,
            accentGreen = FunGreen80,
            accentOrange = Tabasco80,
            accentRed = Monza80,
        )
    }
}

internal val LocalColors = staticCompositionLocalOf { lightColors() }

@Preview(
    group = "Colors light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light theme",
    showBackground = true,
    backgroundColor = 0xEEEEEE,
)
@Preview(
    group = "Colors dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark theme",
    showBackground = true,
    backgroundColor = 0x111111,
)
@Composable
private fun ColorsPreview() {
    FlowTheme(isDynamic = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.spaces.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            mapOf(
                "primary" to AppTheme.colors.primary,
                "onPrimary" to AppTheme.colors.onPrimary,
                "primaryContainer" to AppTheme.colors.primaryContainer,
                "onPrimaryContainer" to AppTheme.colors.onPrimaryContainer,
                "outline" to AppTheme.colors.outline,
                "outlineVariant" to AppTheme.colors.outlineVariant,
                "surface" to AppTheme.colors.surface,
                "onSurface" to AppTheme.colors.onSurface,
                "background" to AppTheme.colors.background,
                "onBackground" to AppTheme.colors.onBackground,
                "error" to AppTheme.colors.error,
                "accentBlue" to AppTheme.colors.accentBlue,
                "accentGreen" to AppTheme.colors.accentGreen,
                "accentOrange" to AppTheme.colors.accentOrange,
                "accentRed" to AppTheme.colors.accentRed,
            ).entries.chunked(2).forEach { chunk ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    chunk.forEach { (name, color) ->
                        Column(
                            modifier = Modifier
                                .padding(AppTheme.spaces.medium)
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(200.dp, 65.dp)
                                    .background(color, AppTheme.shapes.small)
                            )
                            Label(
                                modifier = Modifier.padding(top = AppTheme.spaces.small),
                                text = name,
                                color = AppTheme.colors.onBackground,
                            )
                        }
                    }
                }
            }
        }
    }
}
