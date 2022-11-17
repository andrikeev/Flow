package me.rutrackersearch.app.ui

import android.app.UiModeManager
import android.content.Intent
import android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import flow.designsystem.platform.LocalPlatformType
import flow.designsystem.platform.PlatformType
import flow.ui.platform.DeeplinkHandler
import flow.ui.platform.LocalOpenFileHandler
import flow.ui.platform.LocalOpenLinkHandler
import flow.ui.platform.LocalShareLinkHandler
import flow.ui.platform.OpenFileHandler
import flow.ui.platform.OpenLinkHandler
import flow.ui.platform.ShareLinkHandler
import me.rutrackersearch.app.ui.main.MainScreen
import me.rutrackersearch.app.ui.navigation.Navigation
import me.rutrackersearch.app.ui.platform.DeeplinkHandlerImpl
import me.rutrackersearch.app.ui.platform.OpenFileHandlerImpl
import me.rutrackersearch.app.ui.platform.OpenLinkHandlerImpl
import me.rutrackersearch.app.ui.platform.ShareLinkHandlerImpl

@AndroidEntryPoint
open class MainActivity : ComponentActivity() {

    private lateinit var newIntentListener: (Intent) -> Unit

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
        setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val deeplinkHandler = rememberDeeplinkHandler(navController)
            val linkHandler = rememberOpenLinkHandler(deeplinkHandler)
            val shareLinkHandler = rememberShareLinkHandler()
            val openFileHandler = rememberOpenFileHandler()
            CompositionLocalProvider(
                LocalOpenLinkHandler provides linkHandler,
                LocalShareLinkHandler provides shareLinkHandler,
                LocalOpenFileHandler provides openFileHandler,
                LocalPlatformType provides deviceType,
            ) {
                MainScreen {
                    Navigation(navController)
                }
            }
        }
        addOnNewIntentListener { intent ->
            newIntentListener.invoke(intent)
        }
    }

    @Composable
    private fun rememberDeeplinkHandler(navController: NavHostController): DeeplinkHandler {
        return remember {
            DeeplinkHandlerImpl(navController).apply {
                newIntentListener = { intent ->
                    if (intent.data?.let(::handle) == false && intent.action == Intent.ACTION_VIEW) {
                        startActivity(Intent(Intent.ACTION_VIEW, intent.data))
                    }
                }
            }
        }
    }

    @Composable
    private fun rememberOpenLinkHandler(deeplinkHandler: DeeplinkHandler): OpenLinkHandler {
        val uriHandler = LocalUriHandler.current
        return remember { OpenLinkHandlerImpl(deeplinkHandler, uriHandler) }
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
