package flow.work.workers

import android.content.pm.ServiceInfo
import android.os.Build
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.ListenableWorker.Result.failure
import androidx.work.ListenableWorker.Result.retry
import androidx.work.ListenableWorker.Result.success
import flow.notifications.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

private const val BackgroundWorkNotificationId = 0
private const val RetryAttemptsMaxCount = 3

internal fun NotificationService.createForegroundInfo() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ForegroundInfo(
            BackgroundWorkNotificationId,
            createSyncNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
        )
    } else {
        ForegroundInfo(
            BackgroundWorkNotificationId,
            createSyncNotification(),
        )
    }

internal suspend fun ListenableWorker.runCatching(
    block: suspend CoroutineScope.() -> Unit,
    onFailure: suspend CoroutineScope.() -> Unit = {},
    retryAttemptsMaxCount: Int = RetryAttemptsMaxCount,
): ListenableWorker.Result {
    return runCatching { coroutineScope { block() } }
        .fold(
            onSuccess = { success() },
            onFailure = {
                if (runAttemptCount < retryAttemptsMaxCount) {
                    retry()
                } else {
                    runCatching { coroutineScope { onFailure() } }
                    failure()
                }
            },
        )
}
