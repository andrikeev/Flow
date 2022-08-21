package me.rutrackersearch.data.download

import android.app.DownloadManager
import android.app.DownloadManager.COLUMN_LOCAL_URI
import android.app.DownloadManager.COLUMN_STATUS
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment.DIRECTORY_DOWNLOADS
import me.rutrackersearch.auth.AuthObservable
import me.rutrackersearch.domain.service.TorrentDownloadService
import me.rutrackersearch.network.HostProvider
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TorrentDownloadServiceImpl @Inject constructor(
    private val authObservable: AuthObservable,
    private val context: Context,
    private val hostProvider: HostProvider,
) : TorrentDownloadService {
    override suspend fun downloadTorrent(id: String, title: String): String? {
        return suspendCoroutine { continuation ->
            val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            var downloadId = 0L
            context.registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        val action = intent.action
                        if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                            val completedId =
                                intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
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
                },
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
            val fileName = buildValidFatFilename(title.plus(".torrent"))
            val uri = Uri.parse("$hostProvider/forum/dl.php?t=$id")
            val request = DownloadManager.Request(uri)
                .addRequestHeader("Auth-Token", authObservable.token)
                .setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, fileName)
                .setTitle(title)
                .setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            downloadId = downloadManager.enqueue(request)
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
                val statusColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_STATUS)
                val status = cursor.getInt(statusColumnIndex)
                if (DownloadManager.STATUS_SUCCESSFUL == status) {
                    val uriColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_LOCAL_URI)
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
