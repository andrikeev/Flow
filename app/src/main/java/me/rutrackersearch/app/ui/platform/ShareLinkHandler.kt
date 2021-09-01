package me.rutrackersearch.app.ui.platform

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.staticCompositionLocalOf

interface ShareLinkHandler {
    fun shareLink(link: String)
}

class ShareLinkHandlerImpl(
    private val context: Context
) : ShareLinkHandler {
    override fun shareLink(link: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }
}

val LocalShareLinkHandler = staticCompositionLocalOf<ShareLinkHandler> {
    error("no ShareLinkHandler provided")
}
