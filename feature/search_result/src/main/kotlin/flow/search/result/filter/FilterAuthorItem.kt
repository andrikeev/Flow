package flow.search.result.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import flow.designsystem.component.RunOnComposition
import flow.designsystem.component.TextButton
import flow.designsystem.component.rememberFocusRequester
import flow.designsystem.theme.Border
import flow.models.topic.Author
import flow.search.result.R

@Composable
internal fun FilterAuthorItem(
    selected: Author?,
    onSubmit: (Author?) -> Unit,
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var showDialog by remember { mutableStateOf(false) }
        if (showDialog) {
            AuthorDialog(
                author = selected,
                onDismissRequest = { showDialog = false },
                onSubmit = onSubmit,
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.search_screen_filter_author_label),
        )
        Surface(
            modifier = Modifier.weight(2f),
            shape = MaterialTheme.shapes.small,
            border = Border.outline,
            onClick = { showDialog = true },
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = selected?.name ?: stringResource(R.string.search_screen_filter_any),
            )
        }
    }
}

@Composable
private fun AuthorDialog(
    author: Author?,
    onDismissRequest: () -> Unit,
    onSubmit: (Author?) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        var textValue by remember { mutableStateOf(author?.name.orEmpty()) }
        fun onSubmit() {
            val newAuthor = textValue.takeIf(String::isNotBlank)?.let { Author(name = it) }
            onSubmit(newAuthor)
            onDismissRequest()
        }
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column {
                val focusRequester = rememberFocusRequester()
                RunOnComposition { focusRequester.requestFocus() }
                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 24.dp, top = 16.dp, end = 24.dp)
                        .focusRequester(focusRequester),
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        text = stringResource(flow.designsystem.R.string.designsystem_action_cancel),
                        onClick = { onDismissRequest() },
                    )
                    if (author != null) {
                        TextButton(
                            text = stringResource(flow.designsystem.R.string.designsystem_action_reset),
                            onClick = {
                                onSubmit(null)
                                onDismissRequest()
                            },
                        )
                    }
                    TextButton(
                        text = stringResource(flow.designsystem.R.string.designsystem_action_apply),
                        onClick = { onSubmit() },
                    )
                }
            }
        }
    }
}
