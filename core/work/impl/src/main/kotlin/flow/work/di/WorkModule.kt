package flow.work.di

import androidx.work.WorkManager
import flow.work.api.BackgroundService
import flow.work.impl.WorkBackgroundService
import flow.work.workers.AddFavoriteWorker
import flow.work.workers.LoadFavoritesWorker
import flow.work.workers.RemoveFavoriteWorker
import flow.work.workers.SyncBookmarksWorker
import flow.work.workers.SyncFavoritesWorker
import flow.work.workers.UpdateBookmarkWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for the background work scheduler and workers. Workers are created by the
 * KoinWorkerFactory (set up in :app), which supplies Context/WorkerParameters and resolves
 * the remaining constructor dependencies (domain use cases, NotificationService) from Koin.
 */
val workModule = module {
    single { WorkManager.getInstance(androidContext()) }
    singleOf(::WorkBackgroundService) bind BackgroundService::class

    workerOf(::AddFavoriteWorker)
    workerOf(::LoadFavoritesWorker)
    workerOf(::RemoveFavoriteWorker)
    workerOf(::SyncBookmarksWorker)
    workerOf(::SyncFavoritesWorker)
    workerOf(::UpdateBookmarkWorker)
}
