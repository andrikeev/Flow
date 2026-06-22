package flow.notifications

import android.content.Context

/**
 * Framework-agnostic factory for [NotificationService]. Used by the Hilt bridge in the
 * Android app. This module is Android-specific (platform notifications); a multiplatform
 * abstraction will arrive when it is converted to KMP.
 */
fun createNotificationService(context: Context): NotificationService =
    NotificationServiceImpl(context)
