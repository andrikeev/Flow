package flow.main

import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import flow.designsystem.platform.PlatformType
import flow.designsystem.theme.FlowTheme
import flow.designsystem.utils.rememberSystemBarStyle
import flow.models.settings.Theme

@Composable
fun MainScreen(
    theme: Theme,
    platformType: PlatformType,
    content: @Composable () -> Unit,
) {
    FlowTheme(
        isDark = platformType == PlatformType.TV || theme.isDark(),
        isDynamic = theme.isDynamic(),
    ) {
        val activity = LocalContext.current as ComponentActivity
        val systemBarStyle = rememberSystemBarStyle()
        LaunchedEffect(systemBarStyle) {
            activity.enableEdgeToEdge(
                statusBarStyle = systemBarStyle,
                navigationBarStyle = systemBarStyle,
            )
        }
        content()
    }
}

@Composable
private fun Theme.isDark(): Boolean = when (this) {
    Theme.DARK -> true
    Theme.LIGHT -> false
    Theme.SYSTEM -> isSystemInDarkTheme()
    Theme.DYNAMIC -> isSystemInDarkTheme()
}

private fun Theme.isDynamic(): Boolean = this == Theme.DYNAMIC
