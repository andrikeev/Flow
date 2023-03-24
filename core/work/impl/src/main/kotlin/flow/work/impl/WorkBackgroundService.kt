package flow.work.impl

import android.os.Build
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import flow.models.settings.SyncPeriod
import flow.work.api.BackgroundService
import flow.work.workers.AddFavoriteWorker
import flow.work.workers.DelegatingWorker
import flow.work.workers.LoadFavoritesWorker
import flow.work.workers.RemoveFavoriteWorker
import flow.work.workers.SyncBookmarksWorker
import flow.work.workers.SyncFavoritesWorker
import flow.work.workers.UpdateBookmarkWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

internal class WorkBackgroundService @Inject constructor(
    private val workManager: WorkManager,
) : BackgroundService {
    override suspend fun addFavoriteTopic(id: String, isTorrent: Boolean) {
        val data = DelegatingWorker.delegateData(
            AddFavoriteWorker::class,
            AddFavoriteWorker.workerData(id, isTorrent),
        )
        val workRequest = oneTimeWorkRequest<DelegatingWorker>(data)
        workManager.enqueueUniqueWork(id, ExistingWorkPolicy.REPLACE, workRequest)
    }

    override suspend fun removeFavoriteTopic(id: String) {
        val data = DelegatingWorker.delegateData(
            RemoveFavoriteWorker::class,
            RemoveFavoriteWorker.workerData(id),
        )
        val workRequest = oneTimeWorkRequest<DelegatingWorker>(data)
        workManager.enqueueUniqueWork(id, ExistingWorkPolicy.REPLACE, workRequest)
    }

    override suspend fun updateBookmark(id: String) {
        val data = DelegatingWorker.delegateData(
            UpdateBookmarkWorker::class,
            UpdateBookmarkWorker.workerData(id),
        )
        val workRequest = oneTimeWorkRequest<DelegatingWorker>(data)
        workManager.enqueueUniqueWork(id, ExistingWorkPolicy.REPLACE, workRequest)
    }

    override suspend fun loadFavorites() {
        val data = DelegatingWorker.delegateData(LoadFavoritesWorker::class)
        val workRequest = oneTimeWorkRequest<DelegatingWorker>(data)
        workManager.enqueueUniqueWork(LoadFavoritesWork, ExistingWorkPolicy.REPLACE, workRequest)
    }

    override suspend fun syncFavorites(syncPeriod: SyncPeriod) {
        if (syncPeriod == SyncPeriod.OFF) {
            workManager.cancelUniqueWork(SyncFavoritesWork)
        } else {
            val data = DelegatingWorker.delegateData(SyncFavoritesWorker::class)
            val workRequest = periodicWorkRequest<DelegatingWorker>(syncPeriod, data)
            workManager.enqueueUniquePeriodicWork(
                SyncFavoritesWork,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                workRequest,
            )
        }
    }

    override suspend fun syncBookmarks(syncPeriod: SyncPeriod) {
        if (syncPeriod == SyncPeriod.OFF) {
            workManager.cancelUniqueWork(SyncBookmarksWork)
        } else {
            val data = DelegatingWorker.delegateData(SyncBookmarksWorker::class)
            val workRequest = periodicWorkRequest<DelegatingWorker>(syncPeriod, data)
            workManager.enqueueUniquePeriodicWork(
                SyncBookmarksWork,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                workRequest,
            )
        }
    }

    override suspend fun stopBackgroundWorks() {
        workManager.cancelAllWork()
    }

    private companion object {
        const val LoadFavoritesWork = "LoadFavoritesWork"
        const val SyncFavoritesWork = "SyncFavoritesWork"
        const val SyncBookmarksWork = "SyncBookmarksWork"

        inline fun <reified T : ListenableWorker> oneTimeWorkRequest(
            inputData: Data = Data.EMPTY,
        ) = OneTimeWorkRequestBuilder<T>().apply {
            setInputData(inputData)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            }
            setConstraints(requiredNetworkConstraints())
            setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS,
            )
        }.build()

        inline fun <reified T : ListenableWorker> periodicWorkRequest(
            syncPeriod: SyncPeriod,
            inputData: Data = Data.EMPTY,
        ) = PeriodicWorkRequestBuilder<T>(
            syncPeriod.repeatIntervalMillis, TimeUnit.MILLISECONDS,
            syncPeriod.flexIntervalMillis, TimeUnit.MILLISECONDS,
        ).apply {
            setInputData(inputData)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            }
            setConstraints(requiredNetworkConstraints())
            setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS,
            )
        }.build()

        fun requiredNetworkConstraints(): Constraints {
            return Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        }

        val SyncPeriod.repeatIntervalMillis: Long
            get() = when (this) {
                SyncPeriod.OFF -> Duration.ZERO
                SyncPeriod.HOUR -> 1.hours
                SyncPeriod.SIX_HOURS -> 6.hours
                SyncPeriod.TWELVE_HOURS -> 12.hours
                SyncPeriod.DAY -> 1.days
                SyncPeriod.WEEK -> 7.days
            }.inWholeMilliseconds

        val SyncPeriod.flexIntervalMillis: Long
            get() = when (this) {
                SyncPeriod.OFF -> Duration.ZERO
                SyncPeriod.HOUR -> 15.minutes
                SyncPeriod.SIX_HOURS -> 1.hours
                SyncPeriod.TWELVE_HOURS -> 2.hours
                SyncPeriod.DAY -> 6.days
                SyncPeriod.WEEK -> 1.days
            }.inWholeMilliseconds
    }
}
