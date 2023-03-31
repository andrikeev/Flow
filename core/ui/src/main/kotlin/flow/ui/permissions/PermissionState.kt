package flow.ui.permissions

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionState as AccompanistPermissionState
import com.google.accompanist.permissions.PermissionStatus as AccompanistPermissionStatus

@Stable
interface PermissionState {
    val status: PermissionStatus
    fun requestPermission()
}

@Composable
fun rememberPermissionState(permission: Permission): PermissionState = when (permission) {
    Permission.WriteExternalStorage -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        rememberAlwaysGrantedPermissions()
    } else {
        rememberDelegatePermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    Permission.PostNotifications -> if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        rememberAlwaysGrantedPermissions()
    } else {
        rememberDelegatePermissionState(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Stable
private object AlwaysGrantedPermissions : PermissionState {
    override val status: PermissionStatus = PermissionStatus.Granted
    override fun requestPermission() = Unit
}

@Composable
private fun rememberAlwaysGrantedPermissions(): PermissionState = remember { AlwaysGrantedPermissions }

@Composable
private fun rememberDelegatePermissionState(permission: String): PermissionState {
    val permissionState = rememberPermissionState(permission)
    val delegateState = object : PermissionState {
        override var status = permissionState.status()
        override fun requestPermission() = permissionState.launchPermissionRequest()
    }
    LaunchedEffect(permissionState.status) { delegateState.status = permissionState.status() }
    return delegateState
}

private fun AccompanistPermissionState.status(): PermissionStatus {
    return when (val status = status) {
        is AccompanistPermissionStatus.Denied -> PermissionStatus.Denied(status.shouldShowRationale)
        is AccompanistPermissionStatus.Granted -> PermissionStatus.Granted
    }
}
