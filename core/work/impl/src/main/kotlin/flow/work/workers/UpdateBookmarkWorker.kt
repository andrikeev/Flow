package flow.work.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import flow.domain.usecase.UpdateBookmarkUseCase
import flow.notifications.NotificationService

internal class UpdateBookmarkWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork() = runCatching({ updateBookmarkUseCase(inputData.id) })
    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()

    companion object {
        private const val IdKey = "id"

        private val Data.id: String
            get() = requireNotNull(getString(IdKey))

        fun workerData(id: String): Data.Builder.() -> Unit = {
            putString(IdKey, id)
        }
    }
}
