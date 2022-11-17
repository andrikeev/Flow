package flow.designsystem.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.material.color.ColorRoles
import com.google.android.material.color.MaterialColors
import kotlin.math.ln

@Composable
fun FlowTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    isDynamic: Boolean = isMaterialYouAvailable(),
    content: @Composable () -> Unit,
) {
    if (isDynamic && isMaterialYouAvailable()) {
        DynamicTheme(isDark, content)
    } else {
        AppTheme(isDark, content)
    }
}

fun ColorScheme.surfaceColorAtElevation(elevation: Dp): Color {
    if (elevation == Elevation.zero) return surface
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return surfaceTint.copy(alpha = alpha).compositeOver(surface)
}

fun ColorScheme.surfaceVariantColorAtElevation(
    elevation: Dp,
): Color {
    if (elevation == 0.dp) return surfaceVariant
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return surfaceTint.copy(alpha = alpha).compositeOver(surfaceVariant)
}


@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
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
    primary = Indigo40,
    onPrimary = Indigo100,
    primaryContainer = Indigo90,
    onPrimaryContainer = Indigo10,
    secondary = Studio40,
    onSecondary = Studio100,
    secondaryContainer = Studio90,
    onSecondaryContainer = Studio10,
    tertiary = Lipstick40,
    onTertiary = Lipstick100,
    tertiaryContainer = Lipstick90,
    onTertiaryContainer = Lipstick10,
    error = Thunderbird40,
    errorContainer = Thunderbird90,
    onError = Thunderbird100,
    onErrorContainer = Thunderbird10,
    background = SanMarino99,
    onBackground = SanMarino10,
    surface = SanMarino99,
    onSurface = SanMarino10,
    surfaceVariant = MidGray90,
    onSurfaceVariant = MidGray30,
    outline = MidGray50,
    inverseOnSurface = SanMarino95,
    inverseSurface = SanMarino20,
    inversePrimary = Indigo80,
    surfaceTint = Indigo40,
    outlineVariant = MidGray80,
    scrim = SanMarino0,
)

private val DarkThemeColors = darkColorScheme(
    primary = Indigo80,
    onPrimary = Indigo20,
    primaryContainer = Indigo30,
    onPrimaryContainer = Indigo90,
    secondary = Studio80,
    onSecondary = Studio20,
    secondaryContainer = Studio30,
    onSecondaryContainer = Studio90,
    tertiary = Lipstick80,
    onTertiary = Lipstick20,
    tertiaryContainer = Lipstick30,
    onTertiaryContainer = Lipstick90,
    error = Thunderbird80,
    errorContainer = Thunderbird30,
    onError = Thunderbird20,
    onErrorContainer = Thunderbird90,
    background = MidGray10,
    onBackground = MidGray90,
    surface = MidGray10,
    onSurface = MidGray90,
    surfaceVariant = SanMarino10,
    onSurfaceVariant = SanMarino90,
    outline = MidGray60,
    inverseOnSurface = SanMarino20,
    inverseSurface = SanMarino90,
    inversePrimary = Indigo40,
    surfaceTint = Indigo40,
    outlineVariant = SanMarino30,
    scrim = SanMarino0,
)

private val customColors = arrayOf(
    FunGreen40,
    Denim40,
    Tabasco40,
    Monza40,
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
            val blendedColor = MaterialColors.harmonize(
                customColor.toArgb(),
                colorScheme.primary.toArgb(),
            )
            MaterialColors.getColorRoles(blendedColor, isLight)
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
