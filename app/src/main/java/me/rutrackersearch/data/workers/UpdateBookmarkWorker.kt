package me.rutrackersearch.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
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
class UpdateBookmarkWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: NetworkApi,
    private val dao: BookmarkDao,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        runCatching {
            val id = requireNotNull(inputData.getString(IdKey))
            val entity = dao.getById(id)
            if (entity != null) {
                val topics = api.category(id)
                    .items
                    .mapInstanceOf(ForumItem.Topic::topic)
                    .map(Topic::id)
                dao.insert(
                    entity.copy(
                        topics = topics,
                        newTopics = emptyList(),
                    )
                )
            }
            Result.success()
        }.getOrElse { Result.retry() }
    }

    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()

    companion object {
        private const val IdKey = "id"

        fun dataOf(id: String): Data {
            return Data.Builder().apply {
                putString(IdKey, id)
            }.build()
        }
    }
}
