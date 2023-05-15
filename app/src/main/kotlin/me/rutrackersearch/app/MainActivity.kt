package me.rutrackersearch.app

import android.app.UiModeManager
import android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import flow.designsystem.platform.LocalPlatformType
import flow.designsystem.platform.PlatformType
import flow.logger.api.LoggerFactory
import flow.main.MainScreen
import flow.main.MainViewModel
import flow.models.settings.Theme
import flow.navigation.rememberNavigationController
import flow.ui.platform.LocalLoggerFactory
import flow.ui.platform.LocalOpenFileHandler
import flow.ui.platform.LocalOpenLinkHandler
import flow.ui.platform.LocalShareLinkHandler
import flow.ui.platform.OpenFileHandler
import flow.ui.platform.OpenLinkHandler
import flow.ui.platform.ShareLinkHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.rutrackersearch.app.navigation.MobileNavigation
import me.rutrackersearch.app.platform.OpenFileHandlerImpl
import me.rutrackersearch.app.platform.OpenLinkHandlerImpl
import me.rutrackersearch.app.platform.ShareLinkHandlerImpl
import javax.inject.Inject

@AndroidEntryPoint
open class MainActivity : ComponentActivity() {

    @Inject
    lateinit var loggerFactory: LoggerFactory

    private val viewModel: MainViewModel by viewModels()

    open val deviceType: PlatformType
        get() {
            val uiModeManager = getSystemService(this, UiModeManager::class.java)
            val currentMode = uiModeManager?.currentModeType
            return if (currentMode == UI_MODE_TYPE_TELEVISION) {
                PlatformType.TV
            } else {
                PlatformType.MOBILE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var animationCompleted = false

        lifecycleScope.launch(Dispatchers.Default) {
            delay(800)
            animationCompleted = true
        }

        setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        super.onCreate(savedInstanceState)

        var theme: Theme? by mutableStateOf(null)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.theme
                    .onEach { theme = it }
                    .collect()
            }
        }
        splashScreen.setKeepOnScreenCondition {
            theme == null || !animationCompleted
        }

        setContent {
            theme?.let { theme ->
                val linkHandler = rememberOpenLinkHandler()
                val shareLinkHandler = rememberShareLinkHandler()
                val openFileHandler = rememberOpenFileHandler()
                CompositionLocalProvider(
                    LocalOpenLinkHandler provides linkHandler,
                    LocalShareLinkHandler provides shareLinkHandler,
                    LocalOpenFileHandler provides openFileHandler,
                    LocalPlatformType provides deviceType,
                    LocalLoggerFactory provides loggerFactory,
                ) {
                    val navigationController = rememberNavigationController()
                    MainScreen(
                        theme = theme,
                        platformType = deviceType,
                        content = { MobileNavigation(navigationController) },
                    )
                }
            }
        }
    }

    @Composable
    private fun rememberOpenLinkHandler(): OpenLinkHandler {
        val context = LocalContext.current
        return remember { OpenLinkHandlerImpl(context, loggerFactory) }
    }

    @Composable
    private fun rememberShareLinkHandler(): ShareLinkHandler {
        val context = LocalContext.current
        return remember { ShareLinkHandlerImpl(context) }
    }

    @Composable
    private fun rememberOpenFileHandler(): OpenFileHandler {
        val context = LocalContext.current
        return remember { OpenFileHandlerImpl(context) }
    }
}
