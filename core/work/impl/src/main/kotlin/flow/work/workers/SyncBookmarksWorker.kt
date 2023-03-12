package flow.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import flow.domain.usecase.SyncBookmarksUseCase
import flow.notifications.NotificationService

@HiltWorker
internal class SyncBookmarksWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncBookmarksUseCase: SyncBookmarksUseCase,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork() = runCatching({ syncBookmarksUseCase() })
    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()
}
