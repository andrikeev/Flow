package me.rutrackersearch.app.main

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import flow.designsystem.platform.PlatformType
import flow.designsystem.theme.FlowTheme
import flow.models.settings.Theme

@Composable
fun MainScreen(
    theme: Theme,
    platformType: PlatformType,
    content: @Composable () -> Unit,
) {
    val isDark = when (theme) {
        Theme.DARK -> true
        Theme.LIGHT -> false
        Theme.SYSTEM,
        Theme.DYNAMIC,
        -> isSystemInDarkTheme()
    } || platformType == PlatformType.TV
    val isDynamic = theme == Theme.DYNAMIC
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(isDark) {
        systemUiController.setSystemBarsColor(Color.Transparent, !isDark)
    }
    FlowTheme(
        isDark = isDark,
        isDynamic = isDynamic,
        content = content,
    )
}
