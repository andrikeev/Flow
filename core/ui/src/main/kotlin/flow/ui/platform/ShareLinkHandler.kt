package flow.ui.platform

import androidx.compose.runtime.staticCompositionLocalOf

interface ShareLinkHandler {
    fun shareLink(link: String)
}

val LocalShareLinkHandler = staticCompositionLocalOf<ShareLinkHandler> {
    error("no ShareLinkHandler provided")
}
