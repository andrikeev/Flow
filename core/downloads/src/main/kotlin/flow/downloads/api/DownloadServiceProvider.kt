package flow.downloads.api

import android.content.Context
import flow.downloads.impl.DownloadServiceImpl

/**
 * Framework-agnostic factory for [DownloadService]. Used by the Hilt bridge in the
 * Android app. This module is Android-specific (DownloadManager); a multiplatform
 * abstraction will arrive when it is converted to KMP.
 */
fun createDownloadService(context: Context): DownloadService =
    DownloadServiceImpl(context)
