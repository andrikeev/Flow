package flow.work.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import flow.domain.usecase.LoadFavoritesUseCase
import flow.notifications.NotificationService

internal class LoadFavoritesWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val loadFavoritesUseCase: LoadFavoritesUseCase,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = runCatching({ loadFavoritesUseCase() })
    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()
}
