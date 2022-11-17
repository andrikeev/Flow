package me.rutrackersearch.app.ui.main

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import flow.designsystem.platform.LocalPlatformType
import flow.designsystem.platform.PlatformType
import flow.designsystem.theme.FlowTheme
import flow.models.settings.Theme

@Composable
fun MainScreen(content: @Composable () -> Unit) {
    MainScreen(
        viewModel = hiltViewModel(),
        content = content,
    )
}

@Composable
private fun MainScreen(
    viewModel: MainViewModel,
    content: @Composable () -> Unit,
) {
    val theme by viewModel.theme.collectAsState(null)
    val platformType = LocalPlatformType.current
    val isDark = when (theme) {
        Theme.DARK -> true
        Theme.LIGHT -> false
        Theme.SYSTEM,
        Theme.DYNAMIC,
        null -> isSystemInDarkTheme()
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
