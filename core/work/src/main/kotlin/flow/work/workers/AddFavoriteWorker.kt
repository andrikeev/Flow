package flow.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import flow.auth.api.AuthRepository
import flow.database.dao.FavoriteTopicDao
import flow.database.entity.FavoriteTopicEntity
import flow.network.NetworkApi
import flow.notifications.NotificationService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@HiltWorker
internal class AddFavoriteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkApi: NetworkApi,
    private val authRepository: AuthRepository,
    private val favoriteTopicDao: FavoriteTopicDao,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        val id = inputData.id
        runCatching {
            if (authRepository.isAuthorized()) {
                launch { networkApi.addFavorite(id) }
            }
            if (inputData.isTorrent) {
                launch {
                    runCatching {
                        val torrent = networkApi.torrent(id)
                        favoriteTopicDao.insert(FavoriteTopicEntity.of(torrent))
                    }
                }
            }
            success()
        }.getOrElse {
            retryOrFailure { favoriteTopicDao.deleteById(id) }
        }
    }

    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()

    companion object {
        private const val IdKey = "id"
        private const val IsTorrentKey = "is_torrent"

        private val Data.id: String
            get() = requireNotNull(getString(IdKey))

        private val Data.isTorrent: Boolean
            get() = getBoolean(IsTorrentKey, false)

        fun workerData(id: String, isTorrent: Boolean): Data.Builder.() -> Unit = {
            putString(IdKey, id)
            putBoolean(IsTorrentKey, isTorrent)
        }
    }
}
