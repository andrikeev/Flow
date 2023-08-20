package flow.downloads.impl

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE
import android.app.DownloadManager.COLUMN_LOCAL_URI
import android.app.DownloadManager.COLUMN_STATUS
import android.app.DownloadManager.EXTRA_DOWNLOAD_ID
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.app.DownloadManager.STATUS_SUCCESSFUL
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.StrictMode
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import flow.downloads.api.DownloadRequest
import flow.downloads.api.DownloadService
import java.io.File
import java.net.URI
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DownloadServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : DownloadService {
    private val cache = mutableMapOf<String, String>()

    override suspend fun downloadTorrentFile(downloadRequest: DownloadRequest): String? {
        val cachedUri = cache[downloadRequest.id]
        if (cachedUri != null && File(URI.create(cachedUri)).exists()) {
            return cachedUri
        } else {
            return suspendCoroutine { continuation ->
                context.getSystemService<DownloadManager>()?.let { downloadManager ->
                    val fileName = buildValidFatFilename(downloadRequest.title.plus(".torrent"))
                    val uri = Uri.parse(downloadRequest.uri)
                    val request = DownloadManager.Request(uri).apply {
                        downloadRequest.headers.forEach { (key, value) ->
                            addRequestHeader(key, value)
                        }
                        setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, fileName)
                        setTitle(downloadRequest.title)
                        setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    }
                    val downloadId = runCatching { downloadManager.enqueue(request) }.getOrNull()
                    if (downloadId == null) {
                        continuation.resume(null)
                    } else {
                        context.registerDownloadCompleteReceiver(downloadId) {
                            val fileUri = allowDiskReads {
                                downloadManager.getDownloadedFileUri(downloadId)
                            }
                            if (fileUri != null) {
                                cache[downloadRequest.id] = fileUri
                            }
                            continuation.resume(fileUri)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun Context.registerDownloadCompleteReceiver(
        downloadId: Long,
        onDownloadCompleted: () -> Unit,
    ) {
        val receiver = DownloadCompleteReceiver(downloadId, onDownloadCompleted)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                receiver,
                IntentFilter(ACTION_DOWNLOAD_COMPLETE),
                RECEIVER_EXPORTED,
            )
        } else {
            registerReceiver(
                receiver,
                IntentFilter(ACTION_DOWNLOAD_COMPLETE),
            )
        }
    }

    private class DownloadCompleteReceiver(
        private val downloadId: Long,
        private val onDownloadCompleted: () -> Unit,
    ) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == ACTION_DOWNLOAD_COMPLETE) {
                val completedId = intent.getLongExtra(EXTRA_DOWNLOAD_ID, 0)
                if (completedId == downloadId) {
                    context.unregisterReceiver(this)
                    onDownloadCompleted()
                }
            }
        }
    }

    private fun DownloadManager.getDownloadedFileUri(downloadId: Long): String? = runCatching {
        query(DownloadManager.Query().setFilterById(downloadId))?.use { cursor ->
            if (cursor.moveToFirst()) {
                val statusColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_STATUS)
                val status = cursor.getInt(statusColumnIndex)
                if (STATUS_SUCCESSFUL == status) {
                    val uriColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_LOCAL_URI)
                    cursor.getString(uriColumnIndex)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }.getOrNull()

    private fun <T> allowDiskReads(block: () -> T): T {
        val oldPolicy = StrictMode.allowThreadDiskReads()
        try {
            return block()
        } finally {
            StrictMode.setThreadPolicy(oldPolicy)
        }
    }
}
