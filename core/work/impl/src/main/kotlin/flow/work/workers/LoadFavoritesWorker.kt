package flow.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import flow.domain.usecase.LoadFavoritesUseCase
import flow.notifications.NotificationService

@HiltWorker
internal class LoadFavoritesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val loadFavoritesUseCase: LoadFavoritesUseCase,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = runCatching({ loadFavoritesUseCase() })
    override suspend fun getForegroundInfo() = notificationService.createForegroundInfo()
}
