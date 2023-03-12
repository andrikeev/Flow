package flow.ui.platform

import androidx.compose.runtime.staticCompositionLocalOf

interface OpenFileHandler {
    fun openFile(uri: String)
}

val LocalOpenFileHandler = staticCompositionLocalOf<OpenFileHandler> {
    error("no OpenFileHandler provided")
}
