package flow.ui.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

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
        rememberRuntimePermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    Permission.PostNotifications -> if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        rememberAlwaysGrantedPermissions()
    } else {
        rememberRuntimePermissionState(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Stable
private object AlwaysGrantedPermissions : PermissionState {
    override val status: PermissionStatus = PermissionStatus.Granted
    override fun requestPermission() = Unit
}

@Composable
private fun rememberAlwaysGrantedPermissions(): PermissionState =
    remember { AlwaysGrantedPermissions }

@Composable
private fun rememberRuntimePermissionState(permission: String): PermissionState {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val statusState = remember(permission) {
        mutableStateOf(currentPermissionStatus(context, activity, permission))
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {
        statusState.value = currentPermissionStatus(context, activity, permission)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, permission) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                statusState.value = currentPermissionStatus(context, activity, permission)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    return remember(launcher, permission, statusState) {
        object : PermissionState {
            override val status: PermissionStatus get() = statusState.value
            override fun requestPermission() = launcher.launch(permission)
        }
    }
}

private fun currentPermissionStatus(
    context: Context,
    activity: Activity?,
    permission: String,
): PermissionStatus {
    val granted = ContextCompat.checkSelfPermission(context, permission) ==
        PackageManager.PERMISSION_GRANTED
    return if (granted) {
        PermissionStatus.Granted
    } else {
        val rationale = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
        } ?: false
        PermissionStatus.Denied(shouldShowRationale = rationale)
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
