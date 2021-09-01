package me.rutrackersearch.app.ui.platform

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.content.FileProvider
import java.io.File
import java.net.URI


interface OpenFileHandler {
    fun openFile(uri: String)
}

class OpenFileHandlerImpl(
    private val context: Context
) : OpenFileHandler {
    override fun openFile(uri: String) {
        val fileUri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            File(URI.create(uri)),
        )
        val openFileIntent = Intent(Intent.ACTION_VIEW, fileUri).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chooserIntent = Intent.createChooser(openFileIntent, null)
        context.startActivity(chooserIntent)
    }
}

val LocalOpenFileHandler = staticCompositionLocalOf<OpenFileHandler> {
    error("no OpenFileHandler provided")
}
