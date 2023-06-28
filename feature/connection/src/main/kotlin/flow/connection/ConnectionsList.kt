package flow.connection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import flow.connection.ConnectionsAction.DoneClick
import flow.connection.ConnectionsAction.EditClick
import flow.connection.ConnectionsAction.RemoveEndpoint
import flow.connection.ConnectionsAction.SelectEndpoint
import flow.connection.ConnectionsAction.SubmitEndpoint
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Icon
import flow.designsystem.component.IconButton
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.component.TextField
import flow.designsystem.component.onEnter
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.domain.model.endpoint.EndpointState
import flow.domain.model.endpoint.EndpointStatus
import flow.models.settings.Endpoint
import flow.navigation.viewModel
import org.orbitmvi.orbit.compose.collectAsState
import flow.designsystem.R as DsR

@Composable
fun ConnectionsList() = ConnectionsList(viewModel = viewModel())

@Composable
private fun ConnectionsList(
    viewModel: ConnectionsViewModel,
) {
    val state by viewModel.collectAsState()
    ConnectionsList(
        state = state,
        onAction = viewModel::perform,
    )
}

@Composable
private fun ConnectionsList(
    state: ConnectionsState,
    onAction: (ConnectionsAction) -> Unit,
) = Column {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1.0f)
                .padding(horizontal = AppTheme.spaces.large),
            text = stringResource(R.string.connection_item_title),
            style = AppTheme.typography.headlineSmall,
        )
        if (state.edit) {
            TextButton(
                text = stringResource(DsR.string.designsystem_action_done),
                onClick = { onAction(DoneClick) },
            )
        } else {
            IconButton(
                icon = FlowIcons.Comment,
                contentDescription = "Edit connections list",
                onClick = { onAction(EditClick) },
            )
        }
    }
    state.connections.forEach { endpointState ->
        Endpoint(
            state = state,
            endpointState = endpointState,
            onAction = onAction,
        )
    }
    AddConnectionItem(state, onAction)
}

@Composable
private fun Endpoint(
    state: ConnectionsState,
    endpointState: EndpointState,
    onAction: (ConnectionsAction) -> Unit,
) {
    val (endpoint, selected, status) = endpointState
    val selectable by remember(state.edit, selected, status) {
        derivedStateOf { !state.edit && status == EndpointStatus.Active && !selected }
    }
    Surface(
        modifier = Modifier.defaultMinSize(minHeight = AppTheme.sizes.default),
        onClick = { onAction(SelectEndpoint(endpoint)) },
        enabled = selectable,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Crossfade(
                targetState = state.edit,
                label = "EndpointStateIcon_Crossfade",
            ) { edit ->
                if (edit) {
                    val removable = endpoint is Endpoint.Mirror
                    IconButton(
                        icon = FlowIcons.Remove,
                        contentDescription = "Remove",
                        tint = if (removable) {
                            AppTheme.colors.accentRed
                        } else {
                            AppTheme.colors.outline
                        },
                        enabled = removable,
                        onClick = { onAction(RemoveEndpoint(endpoint)) },
                    )
                } else {
                    IconButton(
                        icon = if (selected) {
                            FlowIcons.Selected
                        } else {
                            FlowIcons.NotSelected
                        },
                        tint = if (selectable || selected) {
                            AppTheme.colors.onSurface
                        } else {
                            AppTheme.colors.outline
                        },
                        enabled = selectable,
                        contentDescription = stringResource(
                            if (selected) {
                                R.string.content_description_endpoint_selected
                            } else {
                                R.string.content_description_endpoint_not_selected
                            }
                        ),
                        onClick = { onAction(SelectEndpoint(endpoint)) },
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = AppTheme.spaces.medium),
            ) {
                BodyLarge(
                    text = stringResource(
                        when (endpoint) {
                            is Endpoint.Proxy -> R.string.connection_endpoint_proxy
                            is Endpoint.Rutracker -> R.string.connection_endpoint_rutracker
                            is Endpoint.Mirror -> R.string.connection_endpoint_mirror
                        }
                    ),
                )
                Text(
                    modifier = Modifier.padding(top = AppTheme.spaces.small),
                    text = endpoint.host,
                    color = AppTheme.colors.outline,
                )
            }
            ConnectionStatusIcon(status)
        }
    }
}

@Composable
private fun AddConnectionItem(
    state: ConnectionsState,
    onAction: (ConnectionsAction) -> Unit
) = AnimatedVisibility(
    visible = state.edit,
    enter = fadeIn() + expandVertically { 0 },
    exit = shrinkVertically { 0 } + fadeOut(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
    fun onSubmit() {
        if (textFieldValue.text.isNotBlank()) {
            keyboardController?.hide()
            onAction(SubmitEndpoint(textFieldValue.text))
            textFieldValue = TextFieldValue()
        }
    }
    TextField(
        modifier = Modifier
            .padding(end = AppTheme.spaces.large)
            .onEnter(::onSubmit)
            .defaultMinSize(minHeight = AppTheme.sizes.default)
            .fillMaxWidth(),
        value = textFieldValue,
        onValueChange = { textFieldValue = it },
        singleLine = true,
        leadingIcon = {
            Icon(
                icon = FlowIcons.Add,
                contentDescription = null,
                tint = AppTheme.colors.outline
            )
        },
        prefix = {
            Text(
                text = "https://",
                color = AppTheme.colors.outline,
            )
        },
        suffix = {
            Text(
                text = "/forum/index.php",
                color = AppTheme.colors.outline,
            )
        },
        supportingText = {
            Text(
                text = "For example: rutracker.net",
                color = AppTheme.colors.outline,
                style = AppTheme.typography.bodySmall,
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Done,
            autoCorrect = false,
        ),
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
    )
}
