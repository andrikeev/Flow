package me.rutrackersearch.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import me.rutrackersearch.app.R
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.topic.Torrent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : NotificationService {

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val updatesChannel = NotificationChannel(
                UPDATES_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = context.getString(R.string.notification_channel_description)
            }
            notificationManager.createNotificationChannel(updatesChannel)

            val foregroundTasksChannel = NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                context.getString(R.string.foreground_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = context.getString(R.string.foreground_channel_description)
            }
            notificationManager.createNotificationChannel(foregroundTasksChannel)
        }
    }

    override fun clearAllNotifications() {
        notificationManager.cancelAll()
    }

    override fun showFavoriteUpdateNotification(torrent: Torrent) {
        val builder = NotificationCompat.Builder(context, UPDATES_CHANNEL_ID).apply {
            setContentTitle(context.getString(R.string.notification_topic_update))
            setContentText(torrent.title)
            setSmallIcon(R.drawable.ic_notification)
            color = 0x8c9eff
            setContentIntent(createPendingIntent(torrent))
            setAutoCancel(true)
        }
        notificationManager.notify(torrent.id.toInt(), builder.build())
    }

    override fun showBookmarkUpdateNotification(category: Category) {
        val builder = NotificationCompat.Builder(context, UPDATES_CHANNEL_ID).apply {
            setContentTitle(context.getString(R.string.notification_bookmark_update))
            setContentText(category.name)
            setSmallIcon(R.drawable.ic_notification)
            color = 0x8c9eff
            setContentIntent(createPendingIntent(category))
            setAutoCancel(true)
        }
        notificationManager.notify(category.id.toInt(), builder.build())
    }

    override fun createForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(
            context,
            FOREGROUND_CHANNEL_ID,
        ).apply {
            setContentTitle(context.getString(R.string.notification_running_task))
            setSmallIcon(R.drawable.ic_notification)
            color = 0x8c9eff
        }.build()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                FOREGROUND_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        } else {
            ForegroundInfo(
                FOREGROUND_NOTIFICATION_ID,
                notification,
            )
        }
    }

    private fun createPendingIntent(torrent: Torrent): PendingIntent? {
        val uri = Uri.parse("http://rutracker.org/forum/viewtopic.php?t=${torrent.id}")
        return createPendingIntent(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun createPendingIntent(category: Category): PendingIntent? {
        val uri = Uri.parse("http://rutracker.org/forum/viewtopic.php?t=${category.id}")
        return createPendingIntent(Intent(Intent.ACTION_VIEW, uri))
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
        const val FOREGROUND_NOTIFICATION_ID = 1
        const val UPDATES_CHANNEL_ID = "me.rutrackersearch.app.notifications"
        const val FOREGROUND_CHANNEL_ID = "me.rutrackersearch.app.notifications_foreground"
    }
}
