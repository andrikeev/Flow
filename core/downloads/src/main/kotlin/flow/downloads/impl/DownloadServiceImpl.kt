package flow.downloads.impl

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import flow.auth.api.TokenProvider
import flow.downloads.api.DownloadService
import java.io.File
import java.net.URI
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DownloadServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenProvider: TokenProvider,
) : DownloadService {
    private val cache = mutableMapOf<String, String>()

    override suspend fun downloadTorrentFile(id: String, title: String): String? {
        val cachedUri = cache.getOrDefault(id, null)
        if (cachedUri != null && File(URI.create(cachedUri)).exists()) {
            return cachedUri
        } else {
            return suspendCoroutine { continuation ->
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                var downloadId = 0L
                context.registerReceiver(
                    object : BroadcastReceiver() {
                        override fun onReceive(context: Context, intent: Intent) {
                            val action = intent.action
                            if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                                val completedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
                                if (completedId == downloadId) {
                                    val uri = downloadManager.getDownloadedFileUri(downloadId)
                                    continuation.resume(uri.toString())
                                } else {
                                    continuation.resume(null)
                                }
                            } else {
                                continuation.resume(null)
                            }
                            context.unregisterReceiver(this)
                        }
                    }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                )
                val fileName = buildValidFatFilename(title.plus(".torrent"))
                val uri = Uri.parse("https://rutracker.org/forum/dl.php?t=$id")
                val request = DownloadManager.Request(uri)
                    .addRequestHeader("Cookie", tokenProvider.getToken())
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName).setTitle(title)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                downloadId = downloadManager.enqueue(request)
            }
        }
    }

    private fun DownloadManager.getDownloadedFileUri(downloadId: Long): Uri? {
        val query = DownloadManager.Query().setFilterById(downloadId)
        var cursor: Cursor? = null
        try {
            cursor = query(query)
            if (cursor == null) {
                return null
            }
            if (cursor.moveToFirst()) {
                val statusColumnIndex = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                val status = cursor.getInt(statusColumnIndex)
                if (DownloadManager.STATUS_SUCCESSFUL == status) {
                    val uriColumnIndex = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)
                    val downloadLocalUri = cursor.getString(uriColumnIndex)
                    return Uri.parse(downloadLocalUri)
                }
            }
        } finally {
            cursor?.close()
        }
        return null
    }
}
