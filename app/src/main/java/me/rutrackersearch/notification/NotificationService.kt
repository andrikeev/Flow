package me.rutrackersearch.notification

import androidx.work.ForegroundInfo
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.topic.Torrent

interface NotificationService {
    fun clearAllNotifications()
    fun showFavoriteUpdateNotification(torrent: Torrent)
    fun showBookmarkUpdateNotification(category: Category)
    fun createForegroundInfo(): ForegroundInfo
}