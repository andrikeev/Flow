package me.rutrackersearch.data.sync

import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.WorkManager
import me.rutrackersearch.data.workers.LoadFavoritesWorker
import me.rutrackersearch.data.workers.periodicWorkRequest
import me.rutrackersearch.domain.service.LoadFavoritesService
import me.rutrackersearch.models.settings.SyncPeriod
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadFavoritesServiceImpl @Inject constructor(
    private val workManager: WorkManager,
) : LoadFavoritesService {

    override fun start() {
        val workRequest = periodicWorkRequest<LoadFavoritesWorker>(SyncPeriod.SIX_HOURS)
        workManager.enqueueUniquePeriodicWork(workName, KEEP, workRequest)
    }

    override fun stop() {
        workManager.cancelUniqueWork(workName)
    }

    private companion object {
        const val workName = "FavoritesLoad"
    }
}
