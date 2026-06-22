package flow.work.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import flow.domain.usecase.SyncFavoritesUseCase
import flow.notifications.NotificationService

internal class SyncFavoritesWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val syncFavoritesUseCase: SyncFavoritesUseCase,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork() = runCatching({ syncFavoritesUseCase() })

    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()
}
