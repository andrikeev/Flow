package me.rutrackersearch.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import me.rutrackersearch.data.database.AppDatabase
import me.rutrackersearch.data.database.entity.FavoriteTopicEntity
import me.rutrackersearch.data.notifications.NotificationService
import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.topic.Torrent
import me.rutrackersearch.domain.repository.FavoritesRepository
import me.rutrackersearch.domain.repository.TorrentRepository

@HiltWorker
class SyncFavoritesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationServices: NotificationService,
    private val favoritesRepository: FavoritesRepository,
    private val torrentRepository: TorrentRepository,
    db: AppDatabase,
) : CoroutineWorker(appContext, workerParams) {

    private val dao = db.favoriteTopicDao()

    override suspend fun doWork(): Result {
        return try {
            favoritesRepository.loadFavorites()
            val favorites = favoritesRepository.observeTopics().first()
            favorites.filterIsInstance<TopicModel<Torrent>>().forEach { model ->
                val torrent = model.data
                val update = torrentRepository.loadTorrent(torrent.id)
                val hasUpdate = torrent.magnetLink != null &&
                    update.magnetLink != null &&
                    torrent.magnetLink != update.magnetLink
                dao.insert(FavoriteTopicEntity.of(update).copy(hasUpdate = hasUpdate))
                if (hasUpdate) {
                    notificationServices.showFavoriteUpdateNotification(torrent)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
