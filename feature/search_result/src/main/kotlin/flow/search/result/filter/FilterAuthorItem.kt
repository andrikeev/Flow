package flow.search.result.filter

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Dialog
import flow.designsystem.component.DialogState
import flow.designsystem.component.Icon
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.component.TextField
import flow.designsystem.component.rememberDialogState
import flow.designsystem.component.rememberFocusRequester
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.FlowTheme
import flow.designsystem.utils.RunOnFirstComposition
import flow.models.topic.Author
import flow.search.result.R

@Composable
internal fun FilterAuthorItem(
    selected: Author?,
    onSubmit: (Author?) -> Unit,
) {
    val dialogState = rememberDialogState()
    AuthorDialog(
        state = dialogState,
        author = selected,
        onDismissRequest = dialogState::hide,
        onSubmit = onSubmit,
    )
    FilterBarItem(
        label = stringResource(R.string.search_screen_filter_author_label),
        onClick = dialogState::show,
    ) {
        BodyLarge(
            modifier = Modifier.weight(1f),
            text = selected?.name?.takeIf(String::isNotBlank)
                ?: stringResource(R.string.search_screen_filter_any),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(icon = FlowIcons.Author, contentDescription = null)
    }
}

@Composable
private fun AuthorDialog(
    state: DialogState,
    author: Author?,
    onDismissRequest: () -> Unit,
    onSubmit: (Author?) -> Unit,
) {
    if (state.visible) {
        var textValue by remember { mutableStateOf(author?.name.orEmpty()) }
        fun onSubmit() {
            val newAuthor = textValue
                .trim()
                .takeIf(String::isNotBlank)
                ?.let { value -> Author(name = value) }
            onSubmit(newAuthor)
            onDismissRequest()
        }
        Dialog(
            icon = { Icon(icon = FlowIcons.Author, contentDescription = null) },
            title = { Text(stringResource(R.string.search_screen_filter_author_label)) },
            text = {
                val focusRequester = rememberFocusRequester()
                RunOnFirstComposition { focusRequester.requestFocus() }
                TextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    singleLine = true,
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text(stringResource(R.string.search_screen_filter_author_label)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        autoCorrect = true,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                )
            },
            confirmButton = {
                TextButton(
                    text = stringResource(flow.designsystem.R.string.designsystem_action_apply),
                    onClick = { onSubmit() },
                )
            },
            dismissButton = {
                TextButton(
                    text = stringResource(flow.designsystem.R.string.designsystem_action_cancel),
                    onClick = { onDismissRequest() },
                )
            },
            onDismissRequest = onDismissRequest,
        )
    }
}

@Preview
@Composable
private fun AuthorDialogPreview() {
    FlowTheme {
        AuthorDialog(DialogState(true), author = null, {}) {}
    }
}
