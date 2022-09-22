package me.rutrackersearch.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import me.rutrackersearch.auth.AuthObservable
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.network.NetworkApi
import me.rutrackersearch.notification.NotificationService

@HiltWorker
class RemoveFavoriteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val authObservable: AuthObservable,
    private val api: NetworkApi,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        runCatching {
            val id = requireNotNull(inputData.getString(IdKey))
            if (authObservable.authorised) {
                api.removeFavorite(id)
            }
            Result.success()
        }.getOrElse { Result.retry() }
    }

    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()

    companion object {
        private const val IdKey = "id"

        fun dataOf(torrent: Topic): Data {
            return Data.Builder().apply {
                putString(IdKey, torrent.id)
            }.build()
        }
    }
}
