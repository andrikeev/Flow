package flow.ui.platform

import androidx.compose.runtime.staticCompositionLocalOf
import flow.logger.api.LoggerFactory

val LocalLoggerFactory = staticCompositionLocalOf<LoggerFactory> {
    error("no LoggerFactory provided")
}
