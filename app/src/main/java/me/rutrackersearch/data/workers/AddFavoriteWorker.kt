package me.rutrackersearch.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.rutrackersearch.auth.AuthObservable
import me.rutrackersearch.data.database.dao.FavoriteTopicDao
import me.rutrackersearch.data.database.entity.FavoriteTopicEntity
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent
import me.rutrackersearch.network.NetworkApi
import me.rutrackersearch.notification.NotificationService

@HiltWorker
class AddFavoriteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: NetworkApi,
    private val authObservable: AuthObservable,
    private val dao: FavoriteTopicDao,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        runCatching {
            val id = requireNotNull(inputData.getString(IdKey))
            if (authObservable.authorised) {
                launch { api.addFavorite(id) }
            }

            runCatching {
                val isTorrent = inputData.getBoolean(IsTorrentKey, false)
                if (isTorrent) {
                    launch {
                        val torrent = api.torrent(id)
                        dao.insert(FavoriteTopicEntity.of(torrent))
                    }
                }
            }
            Result.success()
        }.getOrElse { Result.retry() }
    }

    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()

    companion object {
        private const val IdKey = "id"
        private const val IsTorrentKey = "is_torrent"

        fun dataOf(torrent: Topic): Data {
            return Data.Builder().apply {
                putString(IdKey, torrent.id)
                putBoolean(IsTorrentKey, torrent is Torrent)
            }.build()
        }
    }
}
