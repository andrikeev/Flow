package flow.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.hilt.android.EntryPointAccessors
import flow.work.di.HiltWorkerFactoryEntryPoint
import kotlin.reflect.KClass

/**
 * A worker that delegates sync to another [CoroutineWorker] constructed with a [HiltWorkerFactory].
 * It allows for custom workers in a library module without having to own configuration of the WorkManager singleton.
 */
internal class DelegatingWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val delegateWorker =
        EntryPointAccessors.fromApplication<HiltWorkerFactoryEntryPoint>(appContext)
            .hiltWorkerFactory()
            .createWorker(appContext, inputData.workerClassName, workerParams) as? CoroutineWorker
            ?: throw IllegalArgumentException("Unable to find appropriate worker")

    override suspend fun getForegroundInfo(): ForegroundInfo = delegateWorker.getForegroundInfo()

    override suspend fun doWork(): Result = delegateWorker.doWork()

    companion object {
        private const val WorkerClassName = "WorkerDelegateClassName"

        private val Data.workerClassName: String
            get() = requireNotNull(getString(WorkerClassName))

        fun delegateData(
            workerClass: KClass<out CoroutineWorker>,
            workerData: Data.Builder.() -> Unit = {},
        ): Data {
            return Data.Builder().apply {
                putString(WorkerClassName, workerClass.qualifiedName)
                workerData()
            }.build()
        }
    }
}
