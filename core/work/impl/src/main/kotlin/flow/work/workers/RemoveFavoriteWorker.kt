package flow.work.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import flow.domain.usecase.RemoveRemoteFavoriteUseCase

internal class RemoveFavoriteWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val removeRemoteFavoriteUseCase: RemoveRemoteFavoriteUseCase,
    private val notificationService: flow.notifications.NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork() = runCatching({ removeRemoteFavoriteUseCase.invoke(inputData.id) })
    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()

    companion object {
        private const val IdKey = "id"

        private val Data.id: String
            get() = requireNotNull(getString(IdKey))

        fun workerData(id: String): Data.Builder.() -> Unit = { putString(IdKey, id) }
    }
}
