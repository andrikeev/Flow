package flow.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import flow.common.mapInstanceOf
import flow.database.dao.BookmarkDao
import flow.database.entity.BookmarkEntity
import flow.models.forum.ForumItem
import flow.models.topic.Topic
import flow.network.NetworkApi
import kotlinx.coroutines.coroutineScope

@HiltWorker
internal class SyncBookmarksWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkApi: NetworkApi,
    private val bookmarkDao: BookmarkDao,
    private val notificationService: flow.notifications.NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        bookmarkDao.getAll()
            .onEach { entity -> updateBookmark(entity) }
        success()
    }

    private suspend fun updateBookmark(bookmark: BookmarkEntity) {
        runCatching {
            val (id, _, _, oldTopics, oldNewTopics) = bookmark

            val updateTopics = networkApi.category(id)
                .items
                .mapInstanceOf(ForumItem.Topic::topic)
                .map(Topic::id)

            val newTopics = updateTopics
                .subtract(oldTopics.toSet())
                .plus(oldNewTopics)
                .distinct()

            bookmarkDao.insert(
                bookmark.copy(
                    topics = updateTopics,
                    newTopics = newTopics,
                )
            )

            if (!oldNewTopics.containsAll(newTopics)) {
                notificationService.showBookmarkUpdateNotification(bookmark.category)
            }
        }
    }

    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()
}
