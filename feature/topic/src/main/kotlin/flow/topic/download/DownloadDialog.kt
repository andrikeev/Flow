package flow.topic.download

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flow.designsystem.component.CircularProgressIndicator
import flow.designsystem.component.Dialog
import flow.designsystem.component.Icon
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.designsystem.utils.RunOnFirstComposition
import flow.navigation.viewModel
import flow.topic.R
import flow.ui.permissions.Permission
import flow.ui.permissions.isGranted
import flow.ui.permissions.rememberPermissionState
import flow.ui.permissions.shouldShowRationale
import flow.ui.platform.LocalOpenFileHandler
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import flow.designsystem.R as dsR

@Composable
fun DownloadDialog(
    dismiss: () -> Unit,
    openLogin: () -> Unit,
) {
    DownloadDialog(
        viewModel = viewModel(),
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
    val openFileHandler = LocalOpenFileHandler.current
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DownloadSideEffect.Dismiss -> dismiss()
            is DownloadSideEffect.OpenFile -> {
                openFileHandler.openFile(sideEffect.uri)
                dismiss()
            }

            is DownloadSideEffect.OpenLogin -> {
                openLogin()
                dismiss()
            }
        }
    }
    val state by viewModel.collectAsState()
    DownloadDialog(state, viewModel::perform)
}

@Composable
private fun DownloadDialog(
    state: DownloadDialogState,
    onAction: (DownloadAction) -> Unit,
) = when (state) {
    is DownloadDialogState.Initial -> {
        val permission = rememberPermissionState(Permission.WriteExternalStorage)
        when {
            permission.status.isGranted -> {
                RunOnFirstComposition { onAction(DownloadAction.Download) }
            }

            permission.status.shouldShowRationale -> {
                WriteStoragePermissionRationaleDialog(
                    onOk = permission::requestPermission,
                    dismiss = { onAction(DownloadAction.Dismiss) },
                )
            }

            else -> {
                RunOnFirstComposition { permission.requestPermission() }
            }
        }
    }

    is DownloadDialogState.Unauthorised -> Dialog(
        icon = {
            Icon(
                icon = FlowIcons.Account,
                contentDescription = null,
            )
        },
        title = { Text(stringResource(R.string.topics_login_required_title)) },
        text = { Text(stringResource(R.string.topics_login_required_for_download)) },
        confirmButton = {
            TextButton(
                text = stringResource(flow.designsystem.R.string.designsystem_action_login),
                onClick = { onAction(DownloadAction.LoginClick) },
            )
        },
        dismissButton = {
            TextButton(
                text = stringResource(flow.designsystem.R.string.designsystem_action_cancel),
                onClick = { onAction(DownloadAction.Dismiss) },
            )
        },
        onDismissRequest = { onAction(DownloadAction.Dismiss) },
    )

    is DownloadDialogState.DownloadState -> Dialog(
        icon = {
            when (state) {
                is DownloadDialogState.DownloadState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppTheme.sizes.mediumSmall),
                        strokeWidth = 3.dp,
                    )
                }

                is DownloadDialogState.DownloadState.Completed -> {
                    Icon(
                        icon = FlowIcons.FileDownloadDone,
                        contentDescription = null
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(
                    when (state) {
                        is DownloadDialogState.DownloadState.Loading -> {
                            R.string.topic_file_download_in_progress
                        }

                        is DownloadDialogState.DownloadState.Completed -> {
                            R.string.topic_file_download_completed
                        }
                    }
                ),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            when (state) {
                is DownloadDialogState.DownloadState.Loading -> Unit
                is DownloadDialogState.DownloadState.Completed -> {
                    TextButton(
                        text = stringResource(dsR.string.designsystem_action_open_file),
                        onClick = { onAction(DownloadAction.OpenFile) },
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                text = stringResource(dsR.string.designsystem_action_cancel),
                onClick = { onAction(DownloadAction.Dismiss) },
            )
        },
        onDismissRequest = { onAction(DownloadAction.Dismiss) },
    )
}

@Composable
private fun WriteStoragePermissionRationaleDialog(
    onOk: () -> Unit,
    dismiss: () -> Unit,
) = Dialog(
    icon = {
        Icon(
            icon = FlowIcons.Storage,
            contentDescription = null,
        )
    },
    title = { Text(stringResource(R.string.permission_write_storage_rationale_title)) },
    text = { Text(stringResource(R.string.permission_write_storage_rationale)) },
    confirmButton = {
        TextButton(
            text = stringResource(flow.designsystem.R.string.designsystem_action_ok),
            onClick = {
                dismiss()
                onOk()
            },
        )
    },
    dismissButton = {
        TextButton(
            text = stringResource(flow.designsystem.R.string.designsystem_action_cancel),
            onClick = dismiss,
        )
    },
    onDismissRequest = dismiss,
)

@Preview
@Composable
private fun WriteStoragePermissionRationaleDialogPreview() {
    FlowTheme {
        WriteStoragePermissionRationaleDialog({}, {})
    }
}

@Preview
@Composable
private fun DownloadDialogPreview_Unauthorised() {
    FlowTheme {
        DownloadDialog(DownloadDialogState.Unauthorised) {}
    }
}

@Preview
@Composable
private fun DownloadDialogPreview_Loading() {
    FlowTheme {
        DownloadDialog(DownloadDialogState.DownloadState.Loading) {}
    }
}

@Preview
@Composable
private fun DownloadDialogPreview_Completed() {
    FlowTheme {
        DownloadDialog(DownloadDialogState.DownloadState.Completed("url")) {}
    }
}
