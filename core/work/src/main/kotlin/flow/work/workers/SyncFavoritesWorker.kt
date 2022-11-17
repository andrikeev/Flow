package flow.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import flow.database.dao.FavoriteTopicDao
import flow.database.entity.FavoriteTopicEntity
import flow.network.NetworkApi
import kotlinx.coroutines.coroutineScope

@HiltWorker
internal class SyncFavoritesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkApi: NetworkApi,
    private val favoriteTopicDao: FavoriteTopicDao,
    private val notificationService: flow.notifications.NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        favoriteTopicDao.getAll()
            .filter { it.status != null }
            .forEach { updateTorrent(it) }
        success()
    }

    private suspend fun updateTorrent(entity: FavoriteTopicEntity) {
        runCatching {
            val update = networkApi.torrent(entity.id)
            val hasUpdate = entity.magnetLink != null &&
                    update.magnetLink != null &&
                    entity.magnetLink != update.magnetLink
            favoriteTopicDao.insert(
                entity.copy(
                    title = update.title,
                    author = update.author,
                    category = update.category,
                    tags = update.tags,
                    status = update.status,
                    date = update.date,
                    size = update.size,
                    seeds = update.seeds,
                    leeches = update.leeches,
                    magnetLink = update.magnetLink,
                    hasUpdate = entity.hasUpdate || hasUpdate,
                )
            )
            if (hasUpdate) {
                notificationService.showFavoriteUpdateNotification(update)
            }
        }
    }

    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()
}
