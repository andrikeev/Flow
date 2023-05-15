package flow.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import flow.models.forum.Category
import flow.models.topic.Topic
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : NotificationService {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val notificationIconResId = flow.designsystem.R.drawable.ic_notification

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val updatesChannel = NotificationChannel(
                UPDATES_CHANNEL_ID,
                context.getString(R.string.notification_updates_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = context.getString(R.string.notification_updates_channel_description)
            }
            notificationManager.createNotificationChannel(updatesChannel)

            val foregroundTasksChannel = NotificationChannel(
                SYNC_CHANNEL_ID,
                context.getString(R.string.notification_sync_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = context.getString(R.string.notification_sync_channel_description)
            }
            notificationManager.createNotificationChannel(foregroundTasksChannel)
        }
    }

    override fun clearAllNotifications() {
        notificationManager.cancelAll()
    }

    override fun showFavoriteUpdateNotification(topic: Topic) {
        val notification = NotificationCompat.Builder(context, UPDATES_CHANNEL_ID).apply {
            setContentTitle(context.getString(R.string.notification_topic_update))
            setContentText(topic.title)
            setSmallIcon(notificationIconResId)
            color = NOTIFICATION_ICON_COLOR
            setContentIntent(createPendingIntent(topic))
            setAutoCancel(true)
        }.build()
        showNotification(topic.id.toInt(), notification)
    }

    override fun showBookmarkUpdateNotification(category: Category) {
        val notification = NotificationCompat.Builder(context, UPDATES_CHANNEL_ID).apply {
            setContentTitle(context.getString(R.string.notification_bookmark_update))
            setContentText(category.name)
            setSmallIcon(notificationIconResId)
            color = NOTIFICATION_ICON_COLOR
            setContentIntent(createPendingIntent(category))
            setAutoCancel(true)
        }.build()
        showNotification(category.id.toInt(), notification)
    }

    override fun createSyncNotification(): Notification {
        return NotificationCompat.Builder(context, SYNC_CHANNEL_ID).apply {
            setContentTitle(context.getString(R.string.notification_running_task))
            setSmallIcon(notificationIconResId)
            color = NOTIFICATION_ICON_COLOR
            priority = NotificationCompat.PRIORITY_MIN
        }.build()
    }

    private fun showNotification(id: Int, notification: Notification) {
        if (notificationManager.areNotificationsEnabled()) {
            @Suppress("MissingPermission")
            notificationManager.notify(id, notification)
        }
    }

    private fun createPendingIntent(topic: Topic): PendingIntent? {
        val uri = Uri.parse("https://rutracker.org/forum/viewtopic.php?t=${topic.id}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage(context.packageName)
        }
        return createPendingIntent(intent)
    }

    private fun createPendingIntent(category: Category): PendingIntent? {
        val uri = Uri.parse("https://rutracker.org/forum/viewforum.php?f=${category.id}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage(context.packageName)
        }
        return createPendingIntent(intent)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createPendingIntent(intent: Intent): PendingIntent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, intent, 0)
        }
    }

    private companion object {
        const val NOTIFICATION_ICON_COLOR = 0x8c9eff
        const val UPDATES_CHANNEL_ID = "flow.notifications.updates"
        const val SYNC_CHANNEL_ID = "flow.notifications.sync"
    }
}
