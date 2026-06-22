package flow.work.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import org.koin.androidx.workmanager.factory.KoinWorkerFactory

/**
 * Backwards-compatibility shim for app versions that scheduled periodic sync through a
 * delegating worker.
 *
 * Older builds enqueued the unique periodic jobs `SyncFavoritesWork` / `SyncBookmarksWork`
 * with this class as the worker, storing the real worker's class name in the input data. Those
 * specs are still persisted in the WorkManager database after an update, so the class has to
 * keep existing (and stay instantiable) or WorkManager cannot run them and automatic sync
 * silently breaks until the user re-picks the period.
 *
 * The current scheduler ([flow.work.impl.WorkBackgroundService]) enqueues the concrete workers
 * directly, so nothing new is ever delegated through here — this only services pre-existing
 * persisted jobs. It resolves the delegate via [KoinWorkerFactory] (the same factory the app
 * registers), replacing the old Hilt-based delegation.
 *
 * TODO: remove one release after the Koin migration ships, once persisted specs have been
 *  re-enqueued as concrete workers on the next sync.
 */
internal class DelegatingWorker(
    private val appContext: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val delegateWorker: CoroutineWorker =
        KoinWorkerFactory()
            .createWorker(appContext, inputData.workerClassName, workerParams) as? CoroutineWorker
            ?: throw IllegalArgumentException("Unable to find appropriate worker")

    override suspend fun getForegroundInfo(): ForegroundInfo = delegateWorker.getForegroundInfo()

    override suspend fun doWork(): ListenableWorker.Result = delegateWorker.doWork()

    private companion object {
        const val WorkerClassName = "WorkerDelegateClassName"

        val Data.workerClassName: String
            get() = requireNotNull(getString(WorkerClassName))
    }
}
