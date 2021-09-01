package me.rutrackersearch.data.notifications

import me.rutrackersearch.domain.entity.CategoryModel
import me.rutrackersearch.domain.entity.topic.Torrent

interface NotificationService {
    fun clearAllNotifications()
    fun showFavoriteUpdateNotification(torrent: Torrent)
    fun showBookmarkUpdateNotification(categoryModel: CategoryModel)
}
