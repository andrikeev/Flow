package flow.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import flow.designsystem.R
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.drawables.Icon
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.designsystem.utils.RunOnFirstComposition

@Composable
@NonRestartableComposable
fun Dialog(
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AppTheme.colors.surface,
    iconContentColor: Color = AppTheme.colors.primary,
    titleContentColor: Color = AppTheme.colors.onSurface,
    textContentColor: Color = AppTheme.colors.outline,
    tonalElevation: Dp = AppTheme.elevations.large,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
) = AlertDialog(
    icon = icon,
    title = title,
    text = text,
    shape = shape,
    containerColor = containerColor,
    iconContentColor = iconContentColor,
    titleContentColor = titleContentColor,
    textContentColor = textContentColor,
    tonalElevation = tonalElevation,
    confirmButton = confirmButton,
    dismissButton = dismissButton,
    onDismissRequest = onDismissRequest,
)

@Composable
fun ConfirmationDialog(state: ConfirmationDialogState) {
    return when (val dialogState = state.dialogState) {
        is DialogConfirmationState.Hide -> Unit
        is DialogConfirmationState.Show -> ConfirmationDialog(
            icon = dialogState.icon,
            title = stringResource(dialogState.title),
            message = stringResource(dialogState.text),
            positiveButtonText = stringResource(dialogState.positiveButtonText),
            negativeButtonText = stringResource(dialogState.negativeButtonText),
            onDismiss = dialogState.onDismiss,
            onConfirm = dialogState.onConfirm,
        )
    }
}

@Composable
@NonRestartableComposable
private fun ConfirmationDialog(
    icon: Icon?,
    title: String,
    message: String,
    positiveButtonText: String,
    negativeButtonText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) = Dialog(
    onDismissRequest = onDismiss,
    icon = icon?.let { { Icon(icon = icon, contentDescription = null) } } ,
    title = { Text(title) },
    text = { Text(message) },
    confirmButton = {
        val focusRequester = rememberFocusRequester()
        RunOnFirstComposition { focusRequester.requestFocus() }
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

@Stable
class DialogState(initialState: Boolean) {
    var visible: Boolean by mutableStateOf(initialState)
        private set

    fun show() {
        visible = true
    }

    fun hide() {
        visible = false
    }
}

@Composable
fun rememberDialogState(initialState: Boolean = false) = remember { DialogState(initialState) }

@Stable
class ConfirmationDialogState internal constructor(initialState: DialogConfirmationState) {
    internal var dialogState: DialogConfirmationState by mutableStateOf(initialState)

    fun show(
        icon: Icon? = null,
        @StringRes title: Int,
        @StringRes text: Int,
        @StringRes positiveButtonText: Int = R.string.designsystem_action_yes,
        @StringRes negativeButtonText: Int = R.string.designsystem_action_no,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
    ) {
        dialogState = DialogConfirmationState.Show(
            icon = icon,
            title = title,
            text = text,
            positiveButtonText = positiveButtonText,
            negativeButtonText = negativeButtonText,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
        )
    }

    fun hide() {
        dialogState = DialogConfirmationState.Hide
    }
}

@Composable
fun rememberConfirmationDialogState() = remember { ConfirmationDialogState(DialogConfirmationState.Hide) }

internal sealed interface DialogConfirmationState {
    object Hide : DialogConfirmationState
    data class Show(
        val icon: Icon? = null,
        @StringRes val title: Int,
        @StringRes val text: Int,
        @StringRes val positiveButtonText: Int = R.string.designsystem_action_yes,
        @StringRes val negativeButtonText: Int = R.string.designsystem_action_no,
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit,
    ) : DialogConfirmationState
}

@ThemePreviews
@Composable
private fun DialogPreview() {
    FlowTheme {
        Dialog(
            icon = { Icon(icon = FlowIcons.AppIcon, contentDescription = null) },
            title = { Text("Dialog title") },
            text = { Text("Dialog description text with some explanation or questions to user") },
            confirmButton = { TextButton(text = "Yes!", onClick = {}) },
            dismissButton = { TextButton(text = "Oh no", onClick = {}) },
            onDismissRequest = {},
        )
    }
}

@ThemePreviews
@Composable
private fun ConfirmationDialogPreview() {
    FlowTheme {
        ConfirmationDialog(
            state = ConfirmationDialogState(
                DialogConfirmationState.Show(
                    title = R.string.designsystem_action_login,
                    text = R.string.designsystem_action_login,
                    positiveButtonText = R.string.designsystem_action_ok,
                    negativeButtonText = R.string.designsystem_action_cancel,
                    onConfirm = {},
                    onDismiss = {},
                ),
            )
        )
    }
}
