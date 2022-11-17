package flow.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import flow.designsystem.R
import kotlinx.coroutines.job

@Composable
fun ConfirmationDialog(
    state: ConfirmationDialogState,
    onDismiss: () -> Unit,
) {
    when (state) {
        is ConfirmationDialogState.Hide -> Unit
        is ConfirmationDialogState.Show -> ConfirmationDialog(
            message = stringResource(state.message),
            positiveButtonText = stringResource(state.positiveButtonText),
            negativeButtonText = stringResource(state.negativeButtonText),
            onDismiss = onDismiss,
            onConfirm = state.onConfirm,
        )
    }
}

@Composable
fun ConfirmationDialog(
    message: String,
    positiveButtonText: String,
    negativeButtonText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(message) },
        confirmButton = {
            val focusRequester = rememberFocusRequester()
            RunOnComposition { focusRequester.requestFocus() }
            TextButton(
                modifier = Modifier.focusRequester(focusRequester),
                text = positiveButtonText,
                onClick = {
                    onConfirm()
                    onDismiss()
                },
            )
        },
        dismissButton = {
            TextButton(
                text = negativeButtonText,
                onClick = onDismiss,
            )
        }
    )
}

sealed interface DialogState {
    object Hide : DialogState
    object Show : DialogState
}

sealed interface ConfirmationDialogState {
    object Hide : ConfirmationDialogState
    data class Show(
        @StringRes val message: Int,
        @StringRes val positiveButtonText: Int = R.string.designsystem_action_yes,
        @StringRes val negativeButtonText: Int = R.string.designsystem_action_no,
        val onConfirm: () -> Unit,
    ) : ConfirmationDialogState
}

@Composable
fun RunOnComposition(block: () -> Unit) {
    LaunchedEffect(Unit) {
        coroutineContext.job.invokeOnCompletion { error ->
            if (error == null) {
                block()
            }
        }
    }
}
