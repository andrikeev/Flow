package flow.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import flow.domain.usecase.SyncFavoritesUseCase
import flow.notifications.NotificationService

@HiltWorker
internal class SyncFavoritesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncFavoritesUseCase: SyncFavoritesUseCase,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork() = runCatching({ syncFavoritesUseCase() })
    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()
}
