package me.rutrackersearch.app.ui.deeplink

import android.net.Uri

interface DeeplinkHandler {
    fun handle(uri: Uri): Boolean
}
