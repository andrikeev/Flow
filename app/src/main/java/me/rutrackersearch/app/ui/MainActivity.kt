package me.rutrackersearch.app.ui

import android.app.UiModeManager
import android.content.Intent
import android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.deeplink.DeeplinkHandler
import me.rutrackersearch.app.ui.deeplink.DeeplinkHandlerImpl
import me.rutrackersearch.app.ui.navigation.MobileNavigation
import me.rutrackersearch.app.ui.navigation.TVNavigation
import me.rutrackersearch.app.ui.platform.LocalOpenFileHandler
import me.rutrackersearch.app.ui.platform.LocalOpenLinkHandler
import me.rutrackersearch.app.ui.platform.LocalPlatformType
import me.rutrackersearch.app.ui.platform.LocalShareLinkHandler
import me.rutrackersearch.app.ui.platform.OpenFileHandlerImpl
import me.rutrackersearch.app.ui.platform.OpenLinkHandlerImpl
import me.rutrackersearch.app.ui.platform.PlatformType
import me.rutrackersearch.app.ui.platform.ShareLinkHandlerImpl
import me.rutrackersearch.app.ui.theme.FlowTheme
import me.rutrackersearch.app.ui.theme.isLight
import me.rutrackersearch.domain.entity.settings.Theme

@AndroidEntryPoint
open class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private lateinit var deeplinkHandler: DeeplinkHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController().also { this.navController = it }
            val deeplinkHandler = remember {
                DeeplinkHandlerImpl(navController)
            }.also { this.deeplinkHandler = it }
            val uriHandler = LocalUriHandler.current
            val linkHandler = remember { OpenLinkHandlerImpl(deeplinkHandler, uriHandler) }
            val shareLinkHandler = remember { ShareLinkHandlerImpl(this) }
            val openFileHandler = remember { OpenFileHandlerImpl(this) }
            val deviceType = remember {
                val uiModeManager = getSystemService(this, UiModeManager::class.java)
                val currentMode = uiModeManager?.currentModeType
                if (currentMode == UI_MODE_TYPE_TELEVISION) {
                    PlatformType.TV
                } else {
                    PlatformType.MOBILE
                }
            }
            CompositionLocalProvider(
                LocalOpenLinkHandler provides linkHandler,
                LocalShareLinkHandler provides shareLinkHandler,
                LocalOpenFileHandler provides openFileHandler,
                LocalPlatformType provides deviceType,
            ) {
                val viewModel: MainViewModel = hiltViewModel()
                val settings by viewModel.settings.collectAsState(null)
                val theme = settings?.theme
                val isDarkTheme = when {
                    theme == Theme.DARK || deviceType == PlatformType.TV -> true
                    theme == Theme.LIGHT -> false
                    else -> isSystemInDarkTheme()
                }
                val isDynamic = theme == Theme.DYNAMIC
                FlowTheme(
                    isDark = isDarkTheme,
                    isDynamic = isDynamic,
                ) {
                    val systemUiController = rememberSystemUiController()
                    val systemUiColor = MaterialTheme.colorScheme.surface
                    LaunchedEffect(systemUiColor) {
                        systemUiController.setSystemBarsColor(
                            color = systemUiColor,
                            darkIcons = systemUiColor.isLight(),
                        )
                    }
                    DynamicBox(
                        mobileContent = {
                            MobileNavigation(
                                modifier = Modifier.systemBarsPadding(),
                                navController = navController,
                            )
                        },
                        tvContent = {
                            TVNavigation(
                                modifier = Modifier.systemBarsPadding(),
                                navController = navController,
                            )
                        },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.data?.let(deeplinkHandler::handle) == false) {
            startActivity(Intent(Intent.ACTION_VIEW, intent.data))
        }
    }
}
