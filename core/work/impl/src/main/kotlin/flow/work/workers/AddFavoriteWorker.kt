package flow.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import flow.domain.usecase.AddRemoteFavoriteUseCase
import flow.domain.usecase.RemoveLocalFavoriteUseCase
import flow.notifications.NotificationService

@HiltWorker
internal class AddFavoriteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val addRemoteFavoriteUseCase: AddRemoteFavoriteUseCase,
    private val removeLocalFavoriteUseCase: RemoveLocalFavoriteUseCase,
    private val notificationService: NotificationService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork() = runCatching(
        block = { addRemoteFavoriteUseCase(inputData.id) },
        onFailure = { removeLocalFavoriteUseCase(inputData.id) },
    )

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
