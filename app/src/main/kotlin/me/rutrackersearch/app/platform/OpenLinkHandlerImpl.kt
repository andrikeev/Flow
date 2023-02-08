package me.rutrackersearch.app.platform

import android.net.Uri
import androidx.compose.ui.platform.UriHandler
import flow.ui.platform.DeeplinkHandler
import flow.ui.platform.OpenLinkHandler

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
