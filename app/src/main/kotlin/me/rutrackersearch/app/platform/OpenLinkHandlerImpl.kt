package me.rutrackersearch.app.platform

import androidx.compose.ui.platform.UriHandler
import flow.logger.api.LoggerFactory
import flow.ui.platform.OpenLinkHandler

class OpenLinkHandlerImpl(
    private val uriHandler: UriHandler,
    loggerFactory: LoggerFactory,
) : OpenLinkHandler {
    private val logger = loggerFactory.get("OpenLinkHandlerImpl")

    override fun openLink(link: String) {
        runCatching {
            val url = if (link.startsWith("viewtopic") || link.startsWith("viewforum") || link.startsWith("tracker")) {
                "https://rutracker.org/forum/$link"
            } else {
                link
            }
            logger.d { "Open link=$link; url=$url" }
            uriHandler.openUri(url)
        }.onFailure { error ->
            logger.e(error) { "Error open link=$link" }
        }
    }
}
