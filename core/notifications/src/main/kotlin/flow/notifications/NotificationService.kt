package flow.notifications

import android.app.Notification
import flow.models.forum.Category
import flow.models.topic.Topic

/**
 * Interface for app system notifications management.
 */
interface NotificationService {
    /**
     * Clear all app notifications.
     */
    fun clearAllNotifications()

    /**
     * Show system notification for topic update.
     *
     * @param topic updated topic
     */
    fun showFavoriteUpdateNotification(topic: Topic)

    /**
     * Show system notification for category update.
     *
     * @param category updated category
     */
    fun showBookmarkUpdateNotification(category: Category)

    /**
     * Creates system notification for background sync task.
     *
     * @return notification
     */
    fun createSyncNotification(): Notification
}
