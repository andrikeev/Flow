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
    val isDark = platformType == PlatformType.TV || theme.isDark()
    val isDynamic = theme.isDynamic()
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(isDark) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !isDark,
            isNavigationBarContrastEnforced = false,
            transformColorForLightContent = { Color.Transparent },
        )
    }
    FlowTheme(
        isDark = isDark,
        isDynamic = isDynamic,
        content = content,
    )
}

@Composable
private fun Theme.isDark(): Boolean = when (this) {
    Theme.DARK -> true
    Theme.LIGHT -> false
    Theme.SYSTEM -> isSystemInDarkTheme()
    Theme.DYNAMIC -> isSystemInDarkTheme()
}

private fun Theme.isDynamic(): Boolean = this == Theme.DYNAMIC
