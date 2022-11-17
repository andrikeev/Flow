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
import flow.network.NetworkApi
import kotlinx.coroutines.coroutineScope

@HiltWorker
internal class RemoveFavoriteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val authRepository: AuthRepository,
    private val networkApi: NetworkApi,
    private val notificationService: flow.notifications.NotificationService,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        runCatching {
            val id = requireNotNull(inputData.getString(IdKey))
            if (authRepository.isAuthorized()) {
                networkApi.removeFavorite(id)
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
