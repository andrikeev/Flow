package me.rutrackersearch.data.workers

import android.os.Build
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkRequest
import me.rutrackersearch.models.settings.SyncPeriod
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

inline fun <reified T : ListenableWorker> oneTimeWorkRequest(
    inputData: Data = Data.EMPTY,
) = OneTimeWorkRequestBuilder<T>().apply {
    setInputData(inputData)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
    }
    setRequiredNetworkConstraints()
    setBackoffCriteria(
        BackoffPolicy.LINEAR,
        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
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
    setRequiredNetworkConstraints()
    setBackoffCriteria(
        BackoffPolicy.LINEAR,
        PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
        TimeUnit.MILLISECONDS,
    )
}.build()

fun <B : WorkRequest.Builder<*, *>, W : WorkRequest> WorkRequest.Builder<B, W>.setRequiredNetworkConstraints(): B {
    return setConstraints(requiredNetworkConstraints())
}

private fun requiredNetworkConstraints(): Constraints {
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
