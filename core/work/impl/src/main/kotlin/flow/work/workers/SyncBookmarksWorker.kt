package flow.work.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import flow.domain.usecase.SyncBookmarksUseCase
import flow.notifications.NotificationService

internal class SyncBookmarksWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val syncBookmarksUseCase: SyncBookmarksUseCase,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork() = runCatching({ syncBookmarksUseCase() })

    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()
}
