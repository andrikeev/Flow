package me.rutrackersearch.data.sync

import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.WorkManager
import me.rutrackersearch.data.workers.SyncBookmarksWorker
import me.rutrackersearch.data.workers.periodicWorkRequest
import me.rutrackersearch.domain.service.BookmarksSyncService
import me.rutrackersearch.models.settings.SyncPeriod
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarksSyncServiceImpl @Inject constructor(
    private val workManager: WorkManager,
) : BookmarksSyncService {

    override suspend fun setSyncPeriod(syncPeriod: SyncPeriod) {
        if (syncPeriod == SyncPeriod.OFF) {
            workManager.cancelUniqueWork(workName)
        } else {
            val workRequest = periodicWorkRequest<SyncBookmarksWorker>(syncPeriod)
            workManager.enqueueUniquePeriodicWork(workName, REPLACE, workRequest)
        }
    }

    private companion object {
        const val workName = "BookmarksSync"
    }
}
