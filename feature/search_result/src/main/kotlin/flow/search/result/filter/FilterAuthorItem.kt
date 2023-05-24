package flow.search.result.filter

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.CollectionPreviewParameterProvider
import flow.designsystem.component.Dialog
import flow.designsystem.component.Icon
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.component.TextField
import flow.designsystem.component.ThemePreviews
import flow.designsystem.component.VisibilityState
import flow.designsystem.component.rememberFocusRequester
import flow.designsystem.component.rememberVisibilityState
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
    val dialogState = rememberVisibilityState()
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
    state: VisibilityState,
    author: Author?,
    onDismissRequest: () -> Unit,
    onSubmit: (Author?) -> Unit,
) {
    if (state.visible) {
        val textValue = remember {
            mutableStateOf(
                TextFieldValue(
                    text = author?.name.orEmpty(),
                    selection = TextRange(author?.name?.length ?: 0),
                )
            )
        }

        fun onSubmit() {
            val newAuthor = textValue.value.text.trim()
                .takeIf(String::isNotBlank)
                ?.let { value -> Author(name = value) }
            onSubmit(newAuthor)
            onDismissRequest()
        }

        fun onReset() {
            onSubmit(null)
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
                    value = textValue.value,
                    onValueChange = { textValue.value = it },
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
                    onClick = ::onSubmit,
                )
            },
            dismissButton = {
                TextButton(
                    text = stringResource(flow.designsystem.R.string.designsystem_action_cancel),
                    onClick = ::onReset,
                )
                if (author != null) {
                    TextButton(
                        text = stringResource(flow.designsystem.R.string.designsystem_action_reset),
                        onClick = { onDismissRequest() },
                    )
                }
            },
            onDismissRequest = onDismissRequest,
        )
    }
}

@ThemePreviews
@Composable
private fun FilterAuthorItemPreview(
    @PreviewParameter(FilterAuthorPreviewParamProvider::class) author: Author?,
) {
    FlowTheme {
        Surface {
            FilterAuthorItem(selected = null) {}
        }
    }
}

@ThemePreviews
@Composable
private fun AuthorDialogPreview(
    @PreviewParameter(FilterAuthorPreviewParamProvider::class) author: Author?,
) {
    FlowTheme {
        AuthorDialog(VisibilityState(true), author = author, {}) {}
    }
}

private class FilterAuthorPreviewParamProvider : CollectionPreviewParameterProvider<Author?>(
    null,
    Author("id", "Some author")
)
