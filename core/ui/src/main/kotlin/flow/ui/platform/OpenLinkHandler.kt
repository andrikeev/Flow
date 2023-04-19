package flow.ui.platform

import androidx.compose.runtime.staticCompositionLocalOf

interface OpenLinkHandler {
    fun openLink(link: String)

    companion object {
        object Stub : OpenLinkHandler {
            override fun openLink(link: String) = Unit
        }
    }
}

val LocalOpenLinkHandler = staticCompositionLocalOf<OpenLinkHandler> {
    error("no OpenLinkHandler provided")
}
