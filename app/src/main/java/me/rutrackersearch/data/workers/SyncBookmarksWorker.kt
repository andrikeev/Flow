package me.rutrackersearch.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import me.rutrackersearch.data.database.dao.BookmarkDao
import me.rutrackersearch.models.forum.ForumItem
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.network.NetworkApi
import me.rutrackersearch.notification.NotificationService
import me.rutrackersearch.utils.mapInstanceOf

@HiltWorker
class SyncBookmarksWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: NetworkApi,
    private val dao: BookmarkDao,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        runCatching {
            val bookmarkEntities = dao.getAll()
            bookmarkEntities.forEach { entity ->
                runCatching {
                    val (id, _, _, oldTopics, oldNewTopics) = entity

                    val updateTopics = api.category(id)
                        .items
                        .mapInstanceOf(ForumItem.Topic::topic)
                        .map(Topic::id)

                    val newTopics = updateTopics
                        .subtract(oldTopics.toSet())
                        .plus(oldNewTopics)
                        .distinct()

                    dao.insert(
                        entity.copy(
                            topics = updateTopics,
                            newTopics = newTopics,
                        )
                    )

                    if (!oldNewTopics.containsAll(newTopics)) {
                        notificationService.showBookmarkUpdateNotification(entity.category)
                    }
                }
            }
        }
        Result.success()
    }

    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()
}
