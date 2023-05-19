package me.rutrackersearch.app.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import flow.logger.api.LoggerFactory
import flow.ui.platform.OpenLinkHandler

class OpenLinkHandlerImpl(
    private val context: Context,
    loggerFactory: LoggerFactory,
) : OpenLinkHandler {
    private val logger = loggerFactory.get("OpenLinkHandlerImpl")

    override fun openLink(link: String) {
        runCatching {
            logger.d { "Open link=$link" }
            if (isSupportedLink(link)) {
                val uri = parseUri(link)
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage(context.packageName)
                }
                context.startActivity(intent)
            } else {
                val intent = Intent.createChooser(Intent(Intent.ACTION_VIEW, Uri.parse(link)), null)
                context.startActivity(intent)
            }
        }.onFailure { error ->
            logger.e(error) { "Error open link=$link" }
        }
    }

    private fun parseUri(link: String): Uri {
        val uri = Uri.parse(link)
        return uri.buildUpon().apply {
            if (uri.scheme.isNullOrBlank()) {
                scheme("https")
            }
            if (uri.host.isNullOrBlank()) {
                authority("rutracker.org")
            }
            if (uri.path?.startsWith("forum") == false) {
                path("/forum/${uri.path}")
            }
        }.build()
    }

    private fun isSupportedLink(link: String): Boolean {
        return link.contains("viewtopic.php") ||
                link.contains("viewforum.php") ||
                link.contains("tracker.php")
    }
}
