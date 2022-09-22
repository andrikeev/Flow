package me.rutrackersearch.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import me.rutrackersearch.data.database.dao.FavoriteTopicDao
import me.rutrackersearch.data.database.entity.FavoriteTopicEntity
import me.rutrackersearch.domain.repository.TorrentRepository
import me.rutrackersearch.notification.NotificationService

@HiltWorker
class SyncFavoritesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val torrentRepository: TorrentRepository,
    private val dao: FavoriteTopicDao,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        dao.getAll()
            .filter { it.status != null }
            .forEach { updateTorrent(it) }
        Result.success()
    }

    private suspend fun updateTorrent(entity: FavoriteTopicEntity) {
        runCatching {
            val update = torrentRepository.loadTorrent(entity.id)
            val hasUpdate = entity.magnetLink != null &&
                    update.magnetLink != null &&
                    entity.magnetLink != update.magnetLink
            dao.insert(
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
