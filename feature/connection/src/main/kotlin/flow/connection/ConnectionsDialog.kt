package flow.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Icon
import flow.designsystem.component.ModalBottomSheet
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.domain.model.endpoint.EndpointState
import flow.models.settings.Endpoint
import flow.navigation.viewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun ConnectionsDialog(
    onDismissRequest: () -> Unit,
) = ConnectionsDialog(
    viewModel = viewModel(),
    onDismissRequest = onDismissRequest,
)

@Composable
private fun ConnectionsDialog(
    viewModel: ConnectionsViewModel,
    onDismissRequest: () -> Unit,
) {
    val state by viewModel.collectAsState()
    ConnectionsDialog(
        state = state,
        onAction = viewModel::perform,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun ConnectionsDialog(
    state: Collection<EndpointState>,
    onAction: (ConnectionsAction) -> Unit,
    onDismissRequest: () -> Unit,
) = ModalBottomSheet(onDismissRequest = onDismissRequest) {
    state.forEach { (endpoint, selected, status) ->
        Surface(
            modifier = Modifier.defaultMinSize(minHeight = AppTheme.sizes.default),
            onClick = { onAction(ConnectionsAction.SelectEndpoint(endpoint)) },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier
                        .padding(AppTheme.spaces.large)
                        .size(AppTheme.sizes.mediumSmall),
                    icon = if (selected) FlowIcons.Selected else FlowIcons.NotSelected,
                    contentDescription = stringResource(
                        if (selected) {
                            R.string.content_description_endpoint_selected
                        } else {
                            R.string.content_description_endpoint_not_selected
                        }
                    ),
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = AppTheme.spaces.medium),
                ) {
                    BodyLarge(
                        text = stringResource(
                            when (endpoint) {
                                Endpoint.Proxy -> R.string.connection_endpoint_proxy
                                Endpoint.RutrackerOrg -> R.string.connection_endpoint_rutracker_org
                                Endpoint.RutrackerNet -> R.string.connection_endpoint_rutracker_net
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
}
