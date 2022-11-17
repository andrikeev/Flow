package flow.ui.platform

import androidx.compose.runtime.staticCompositionLocalOf

interface OpenLinkHandler {
    fun openLink(link: String)
}

val LocalOpenLinkHandler = staticCompositionLocalOf<OpenLinkHandler> {
    error("no OpenLinkHandler provided")
}
