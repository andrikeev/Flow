package flow.work.workers

import android.content.pm.ServiceInfo
import android.os.Build
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.ListenableWorker.Result.failure
import androidx.work.ListenableWorker.Result.retry
import flow.notifications.NotificationService

private const val BackgroundWorkNotificationId = 0
private const val MaxRetryAttemptsCount = 3

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

internal inline fun ListenableWorker.retryOrFailure(
    onFailure: () -> Unit = {},
): ListenableWorker.Result = if (runAttemptCount < MaxRetryAttemptsCount) {
    retry()
} else {
    onFailure()
    failure()
}
