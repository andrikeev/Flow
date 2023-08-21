package flow.ui.permissions

import androidx.compose.runtime.Stable

enum class Permission {
    WriteExternalStorage,
    PostNotifications,
}

@Stable
sealed interface PermissionStatus {
    data object Granted : PermissionStatus
    data class Denied(val shouldShowRationale: Boolean) : PermissionStatus
}

val PermissionStatus.isGranted: Boolean
    get() = this == PermissionStatus.Granted

val PermissionStatus.shouldShowRationale: Boolean
    get() = when (this) {
        PermissionStatus.Granted -> false
        is PermissionStatus.Denied -> shouldShowRationale
    }
