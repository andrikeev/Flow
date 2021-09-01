package me.rutrackersearch.app.ui.common

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.job
import me.rutrackersearch.app.R

@Composable
fun ConfirmationDialog(
    state: ConfirmationDialogState,
    onDismiss: () -> Unit,
) {
    when (state) {
        ConfirmationDialogState.Hide -> Unit
        is ConfirmationDialogState.Show -> ConfirmationDialog(
            text = stringResource(state.message),
            onDismiss = onDismiss,
            onConfirm = state.onConfirm,
        )
    }
}

@Composable
private fun ConfirmationDialog(
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(text) },
        confirmButton = {
            TextButton(
                text = stringResource(R.string.action_yes),
                onClick = {
                    onConfirm()
                    onDismiss()
                },
            )
        },
        dismissButton = {
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                coroutineContext.job.invokeOnCompletion { error ->
                    if (error == null) {
                        focusRequester.requestFocus()
                    }
                }
            }
            TextButton(
                modifier = Modifier.focusRequester(focusRequester),
                text = stringResource(R.string.action_no),
                onClick = onDismiss,
            )
        }
    )
}

sealed interface ConfirmationDialogState {
    object Hide : ConfirmationDialogState
    data class Show(
        @StringRes val message: Int,
        val onConfirm: () -> Unit,
    ) : ConfirmationDialogState
}

@Composable
fun LoginDialog(
    state: LoginDialogState,
    onDismiss: () -> Unit,
) {
    when (state) {
        LoginDialogState.Hide -> Unit
        is LoginDialogState.Show -> AlertDialog(
            text = { Text(stringResource(state.message)) },
            confirmButton = {
                TextButton(
                    text = stringResource(R.string.action_login),
                    onClick = state.onLoginClick,
                )
            },
            dismissButton = {
                TextButton(
                    text = stringResource(R.string.action_cancel),
                    onClick = onDismiss,
                )
            },
            onDismissRequest = onDismiss,
        )
    }
}

sealed interface LoginDialogState {
    object Hide : LoginDialogState
    data class Show(
        @StringRes val message: Int,
        val onLoginClick: () -> Unit,
    ) : LoginDialogState
}
