package me.rutrackersearch.data.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import me.rutrackersearch.models.settings.SyncPeriod
import me.rutrackersearch.domain.service.FavoritesSyncService
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesSyncServiceImpl @Inject constructor(
    appContext: Context,
) : FavoritesSyncService {

    private val workManager = WorkManager.getInstance(appContext)

    override suspend fun setSyncPeriod(syncPeriod: SyncPeriod) {
        if (syncPeriod == SyncPeriod.OFF) {
            workManager.cancelAllWorkByTag(tag)
        } else {
            val workRequest = PeriodicWorkRequestBuilder<SyncFavoritesWorker>(
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
        const val tag = "FavoritesSync"
    }
}
