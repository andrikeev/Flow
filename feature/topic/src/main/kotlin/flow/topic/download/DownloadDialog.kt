package flow.topic.download

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import flow.designsystem.component.TextButton
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.Elevation
import flow.ui.platform.LocalOpenFileHandler
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import flow.designsystem.R as DesignsystemR
import flow.ui.R as UiR


@Composable
fun DownloadDialog(
    dismiss: () -> Unit,
    openLogin: () -> Unit,
) {
    DownloadDialog(
        viewModel = hiltViewModel(),
        dismiss = dismiss,
        openLogin = openLogin,
    )
}

@Composable
private fun DownloadDialog(
    viewModel: DownloadViewModel,
    dismiss: () -> Unit,
    openLogin: () -> Unit,
) {
    val context = LocalContext.current
    val openFileHandler = LocalOpenFileHandler.current
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DownloadSideEffect.Dismiss -> dismiss()
            is DownloadSideEffect.OpenFile -> {
                openFileHandler.openFile(sideEffect.uri)
                dismiss()
            }

            is DownloadSideEffect.OpenLogin -> openLogin()
            is DownloadSideEffect.OpenSettings -> openAppSettings(context)
        }
    }
    val state by viewModel.collectAsState()
    DownloadDialog(state, viewModel::perform)
}

@Composable
private fun DownloadDialog(
    state: DownloadState,
    onAction: (flow.topic.download.DownloadAction) -> Unit,
) {
    val diskPermissionState =
        rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    when (state) {
        is DownloadState.Initial -> {
            if (
                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                !diskPermissionState.status.isGranted
            ) {
                LaunchedEffect(Unit) {
                    diskPermissionState.launchPermissionRequest()
                }
                if (diskPermissionState.status.shouldShowRationale) {
                    AlertDialog(
                        text = { Text(stringResource(UiR.string.topic_permission_required)) },
                        confirmButton = {
                            TextButton(
                                text = stringResource(DesignsystemR.string.designsystem_action_open_settings),
                                onClick = { onAction(flow.topic.download.DownloadAction.SettingsClick) },
                            )
                        },
                        dismissButton = {
                            TextButton(
                                text = stringResource(DesignsystemR.string.designsystem_action_cancel),
                                onClick = { onAction(flow.topic.download.DownloadAction.Dismiss) },
                            )
                        },
                        onDismissRequest = { onAction(flow.topic.download.DownloadAction.Dismiss) },
                    )
                }
            } else {
                LaunchedEffect(Unit) {
                    onAction(flow.topic.download.DownloadAction.Dismiss)
                }
            }
        }

        is DownloadState.Loading -> Dialog(onDismissRequest = { onAction(flow.topic.download.DownloadAction.Dismiss) }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 18.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = FlowIcons.FileDownloading,
                                contentDescription = null
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(horizontal = 16.dp)
                                    .weight(1f),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                )
                            }
                        }
                    }
                    Surface(tonalElevation = Elevation.small) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(
                                text = stringResource(DesignsystemR.string.designsystem_action_cancel),
                                onClick = { onAction(flow.topic.download.DownloadAction.Dismiss) },
                            )
                        }
                    }
                }
            }
        }

        is DownloadState.Completed -> Dialog(onDismissRequest = { onAction(flow.topic.download.DownloadAction.Dismiss) }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 18.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = FlowIcons.FileDownloadDone,
                                contentDescription = null
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(horizontal = 16.dp)
                                    .weight(1f),
                                contentAlignment = Alignment.CenterStart,
                                content = { Text(text = stringResource(UiR.string.topic_file_download_completed)) },
                            )
                        }
                    }
                    Surface(tonalElevation = Elevation.small) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(
                                text = stringResource(DesignsystemR.string.designsystem_action_cancel),
                                onClick = { onAction(flow.topic.download.DownloadAction.Dismiss) },
                            )
                            TextButton(
                                text = stringResource(DesignsystemR.string.designsystem_action_open_file),
                                onClick = { onAction(flow.topic.download.DownloadAction.OpenFile) },
                            )
                        }
                    }
                }
            }
        }

        is DownloadState.Unauthorised -> AlertDialog(
            text = { Text(stringResource(UiR.string.topic_login_required)) },
            confirmButton = {
                TextButton(
                    text = stringResource(DesignsystemR.string.designsystem_action_login),
                    onClick = { onAction(flow.topic.download.DownloadAction.LoginClick) },
                )
            },
            dismissButton = {
                TextButton(
                    text = stringResource(DesignsystemR.string.designsystem_action_cancel),
                    onClick = { onAction(flow.topic.download.DownloadAction.Dismiss) },
                )
            },
            onDismissRequest = { onAction(flow.topic.download.DownloadAction.Dismiss) },
        )
    }
}

private fun openAppSettings(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse("package:${context.packageName}")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    }
    val openSettingsIntent = Intent.createChooser(intent, null)
    context.startActivity(openSettingsIntent)
}