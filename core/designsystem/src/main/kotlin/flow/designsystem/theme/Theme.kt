package flow.designsystem.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun FlowTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    isDynamic: Boolean = isMaterialYouAvailable(),
    content: @Composable () -> Unit,
) {
    val colors = if (isDynamic && isMaterialYouAvailable()) {
        val context = LocalContext.current
        dynamicColors(context, isDark)
    } else {
        if (isDark) {
            darkColors()
        } else {
            lightColors()
        }
    }
    CompositionLocalProvider(LocalColors provides colors, content = content)
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isMaterialYouAvailable() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Preview(
    group = "Text Styles",
    showBackground = true,
)
@Composable
private fun TextStylesPreview() {
    Column(modifier = Modifier.padding(AppTheme.spaces.large)) {
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "displayLarge") }
        ProvideTextStyle(AppTheme.typography.displayLarge) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "displayMedium") }
        ProvideTextStyle(AppTheme.typography.displayMedium) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "displaySmall") }
        ProvideTextStyle(AppTheme.typography.displaySmall) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "headlineLarge") }
        ProvideTextStyle(AppTheme.typography.headlineLarge) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "headlineMedium") }
        ProvideTextStyle(AppTheme.typography.headlineMedium) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "headlineSmall") }
        ProvideTextStyle(AppTheme.typography.headlineSmall) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "titleLarge") }
        ProvideTextStyle(AppTheme.typography.titleLarge) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "titleMedium") }
        ProvideTextStyle(AppTheme.typography.titleMedium) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "titleSmall") }
        ProvideTextStyle(AppTheme.typography.titleSmall) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "bodyLarge") }
        ProvideTextStyle(AppTheme.typography.bodyLarge) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "bodyMedium") }
        ProvideTextStyle(AppTheme.typography.bodyMedium) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "bodySmall") }
        ProvideTextStyle(AppTheme.typography.bodySmall) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "labelLarge") }
        ProvideTextStyle(AppTheme.typography.labelLarge) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "labelMedium") }
        ProvideTextStyle(AppTheme.typography.labelMedium) { Text(text = "Hello, World!") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "labelSmall") }
        ProvideTextStyle(AppTheme.typography.labelSmall) { Text(text = "Hello, World!") }
    }
}
