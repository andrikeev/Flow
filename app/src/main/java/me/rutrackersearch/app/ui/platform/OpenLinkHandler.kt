package me.rutrackersearch.app.ui.platform

import android.net.Uri
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.UriHandler
import me.rutrackersearch.app.ui.deeplink.DeeplinkHandler

interface OpenLinkHandler {
    fun openLink(link: String)
}

class OpenLinkHandlerImpl(
    private val deeplinkHandler: DeeplinkHandler,
    private val uriHandler: UriHandler,
) : OpenLinkHandler {
    override fun openLink(link: String) {
        val uri = Uri.parse(link)
        if (!deeplinkHandler.handle(uri)) {
            runCatching { uriHandler.openUri(link) }
        }
    }
}

val LocalOpenLinkHandler = staticCompositionLocalOf<OpenLinkHandler> {
    error("no OpenLinkHandler provided")
}
