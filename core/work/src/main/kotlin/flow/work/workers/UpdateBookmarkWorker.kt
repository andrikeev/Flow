package flow.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import flow.common.mapInstanceOf
import flow.database.dao.BookmarkDao
import flow.models.forum.ForumItem
import flow.models.topic.Topic
import flow.network.NetworkApi
import kotlinx.coroutines.coroutineScope

@HiltWorker
internal class UpdateBookmarkWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkApi: NetworkApi,
    private val bookmarkDao: BookmarkDao,
    private val notificationService: flow.notifications.NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        runCatching {
            val id = requireNotNull(inputData.getString(IdKey))
            val entity = bookmarkDao.getById(id)
            if (entity != null) {
                val topics = networkApi.category(id)
                    .items
                    .mapInstanceOf(ForumItem.Topic::topic)
                    .map(Topic::id)
                bookmarkDao.insert(
                    entity.copy(
                        topics = topics,
                        newTopics = emptyList(),
                    )
                )
            }
            success()
        }.getOrElse {
            retryOrFailure()
        }
    }

    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()

    companion object {
        private const val IdKey = "id"

        fun workerData(id: String): Data.Builder.() -> Unit = {
            putString(IdKey, id)
        }
    }
}
