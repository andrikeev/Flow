package me.rutrackersearch.data.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import me.rutrackersearch.domain.entity.settings.SyncPeriod
import me.rutrackersearch.domain.service.BookmarksSyncService
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarksSyncServiceImpl @Inject constructor(
    appContext: Context,
) : BookmarksSyncService {

    private val workManager = WorkManager.getInstance(appContext)

    override suspend fun setSyncPeriod(syncPeriod: SyncPeriod) {
        if (syncPeriod == SyncPeriod.OFF) {
            workManager.cancelAllWorkByTag(tag)
        } else {
            val workRequest = PeriodicWorkRequestBuilder<SyncBookmarksWorker>(
                syncPeriod.repeatIntervalMillis, TimeUnit.MILLISECONDS,
                syncPeriod.flexIntervalMillis, TimeUnit.MILLISECONDS,
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS,
                )
                .addTag(tag)
                .build()
            workManager.enqueue(workRequest)
        }
    }

    private companion object {
        const val tag = "BookmarksSync"
    }
}
