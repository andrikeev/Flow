package me.rutrackersearch.app.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.material.color.ColorRoles
import com.google.android.material.color.MaterialColors
import me.rutrackersearch.app.ui.common.ContentElevation
import me.rutrackersearch.domain.entity.settings.Theme
import kotlin.math.ln

@Composable
fun FlowTheme(
    isDark: Boolean,
    isDynamic: Boolean,
    content: @Composable () -> Unit,
) {
    if (isDynamic && isMaterialYouAvailable()) {
        DynamicTheme(isDark, content)
    } else {
        AppTheme(isDark, content)
    }
}

fun Theme.Companion.availableThemes() = if (isMaterialYouAvailable()) {
    Theme.values()
} else {
    arrayOf(Theme.SYSTEM, Theme.DARK, Theme.LIGHT)
}

fun ColorScheme.surfaceColorAtElevation(elevation: Dp): Color {
    if (elevation == ContentElevation.zero) return surface
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return surfaceTint.copy(alpha = alpha).compositeOver(surface)
}

fun Color.isLight() = this.luminance() > 0.5

private fun isMaterialYouAvailable(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}

@Composable
private fun AppTheme(
    isDark: Boolean,
    content: @Composable () -> Unit,
) {
    val colors = if (isDark) {
        DarkThemeColors
    } else {
        LightThemeColors
    }
    val colorsWithHarmonizedError = setupErrorColors(colors, !isDark)
    val extendedColors = setupCustomColors(colors, !isDark)
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorsWithHarmonizedError,
            typography = FlowTypography,
            content = content
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun DynamicTheme(
    isDark: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colors = if (isDark) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }
    val colorsWithHarmonizedError = setupErrorColors(colors, !isDark)
    val extendedColors = setupCustomColors(colors, !isDark)
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorsWithHarmonizedError,
            typography = FlowTypography,
            content = content
        )
    }
}

private val LightThemeColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
)

private val DarkThemeColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
)

private data class CustomColor(
    val color: Color,
    val harmonized: Boolean,
)

private val customColors = arrayOf(
    CustomColor(customGreen, customGreenHarmonize),
    CustomColor(customBlue, customBlueHarmonize),
    CustomColor(customOrange, customOrangeHarmonize),
    CustomColor(customRed, customRedHarmonize),
)

private fun setupErrorColors(colorScheme: ColorScheme, isLight: Boolean): ColorScheme {
    val harmonizedError =
        MaterialColors.harmonize(colorScheme.error.toArgb(), colorScheme.primary.toArgb())
    val roles = MaterialColors.getColorRoles(harmonizedError, isLight)
    return colorScheme.copy(
        error = Color(roles.accent),
        onError = Color(roles.onAccent),
        errorContainer = Color(roles.accentContainer),
        onErrorContainer = Color(roles.onAccentContainer),
    )
}

private fun setupCustomColors(colorScheme: ColorScheme, isLight: Boolean): ExtendedColors {
    return ExtendedColors(
        customColors.map { customColor ->
            if (customColor.harmonized) {
                val blendedColor = MaterialColors.harmonize(
                    customColor.color.toArgb(),
                    colorScheme.primary.toArgb(),
                )
                MaterialColors.getColorRoles(blendedColor, isLight)
            } else {
                MaterialColors.getColorRoles(customColor.color.toArgb(), isLight)
            }
        }.toTypedArray()
    )
}

private class ExtendedColors(val colors: Array<ColorRoles>)

private val LocalExtendedColors = staticCompositionLocalOf<ExtendedColors> {
    error("no ExtendedColors provided")
}

object TopicColors {
    val statusOk: Color
        @Composable
        get() = Color(LocalExtendedColors.current.colors[0].accent)
    val statusOkVariant: Color
        @Composable
        get() = Color(LocalExtendedColors.current.colors[1].accent)
    val statusWarning: Color
        @Composable
        get() = Color(LocalExtendedColors.current.colors[2].accent)
    val statusError: Color
        @Composable
        get() = Color(LocalExtendedColors.current.colors[3].accent)
    val seeds: Color
        @Composable
        get() = Color(LocalExtendedColors.current.colors[0].accent)
    val leaches: Color
        @Composable
        get() = Color(LocalExtendedColors.current.colors[3].accent)
    val file: Color
        @Composable
        get() = Color(LocalExtendedColors.current.colors[1].accent)
    val magnet: Color
        @Composable
        get() = Color(LocalExtendedColors.current.colors[3].accent)
    val torrent: Color
        @Composable
        get() = Color(LocalExtendedColors.current.colors[1].accent)
    val comments: Color
        @Composable
        get() = Color(LocalExtendedColors.current.colors[2].accent)
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun TextStyles_Preview() {
    Column(modifier = Modifier.padding(16.dp)) {
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "displayLarge") }
        ProvideTextStyle(MaterialTheme.typography.displayLarge) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "displayMedium") }
        ProvideTextStyle(MaterialTheme.typography.displayMedium) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "displaySmall") }
        ProvideTextStyle(MaterialTheme.typography.displaySmall) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "headlineLarge") }
        ProvideTextStyle(MaterialTheme.typography.headlineLarge) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "headlineMedium") }
        ProvideTextStyle(MaterialTheme.typography.headlineMedium) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "headlineSmall") }
        ProvideTextStyle(MaterialTheme.typography.headlineSmall) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "titleLarge") }
        ProvideTextStyle(MaterialTheme.typography.titleLarge) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "titleMedium") }
        ProvideTextStyle(MaterialTheme.typography.titleMedium) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "titleSmall") }
        ProvideTextStyle(MaterialTheme.typography.titleSmall) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "bodyLarge") }
        ProvideTextStyle(MaterialTheme.typography.bodyLarge) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "bodyMedium") }
        ProvideTextStyle(MaterialTheme.typography.bodyMedium) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "bodySmall") }
        ProvideTextStyle(MaterialTheme.typography.bodySmall) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "labelLarge") }
        ProvideTextStyle(MaterialTheme.typography.labelLarge) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "labelMedium") }
        ProvideTextStyle(MaterialTheme.typography.labelMedium) { Text(text = "Hello, World!") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "labelSmall") }
        ProvideTextStyle(MaterialTheme.typography.labelSmall) { Text(text = "Hello, World!") }
    }
}