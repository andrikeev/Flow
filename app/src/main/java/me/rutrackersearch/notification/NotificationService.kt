package me.rutrackersearch.notification

import me.rutrackersearch.models.forum.CategoryModel
import me.rutrackersearch.models.topic.Torrent

interface NotificationService {
    fun clearAllNotifications()
    fun showFavoriteUpdateNotification(torrent: Torrent)
    fun showBookmarkUpdateNotification(categoryModel: CategoryModel)
}