package me.rutrackersearch.notification

import android.annotation.SuppressLint
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
import me.rutrackersearch.app.R
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.forum.CategoryModel
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
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun clearAllNotifications() {
        notificationManager.cancelAll()
    }

    override fun showFavoriteUpdateNotification(torrent: Torrent) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setContentTitle(context.getString(R.string.notification_topic_update))
            setContentText(torrent.title)
            setSmallIcon(R.drawable.ic_notification)
            color = 0x8c9eff
            setContentIntent(createPendingIntent(torrent))
            setAutoCancel(true)
        }
        notificationManager.notify(torrent.id.toInt(), builder.build())
    }

    override fun showBookmarkUpdateNotification(categoryModel: CategoryModel) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setContentTitle(context.getString(R.string.notification_bookmark_update))
            setContentText(categoryModel.data.name)
            setSmallIcon(R.drawable.ic_notification)
            color = 0x8c9eff
            setContentIntent(createPendingIntent(categoryModel.data))
            setAutoCancel(true)
        }
        notificationManager.notify(categoryModel.data.id.toInt(), builder.build())
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
        const val CHANNEL_ID = "me.rutrackersearch.app.notifications"
    }
}
