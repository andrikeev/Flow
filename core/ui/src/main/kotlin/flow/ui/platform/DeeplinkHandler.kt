package flow.ui.platform

import android.net.Uri

interface DeeplinkHandler {
    fun handle(uri: Uri): Boolean
}
