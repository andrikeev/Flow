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
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.domain.model.endpoint.EndpointState
import flow.domain.model.endpoint.EndpointStatus
import flow.models.settings.Endpoint
import flow.navigation.viewModel
import org.orbitmvi.orbit.compose.collectAsState

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
    state: Collection<EndpointState>,
    onAction: (ConnectionsAction) -> Unit,
) = Column {
    state.forEach { (endpoint, selected, status) ->
        Surface(
            modifier = Modifier.defaultMinSize(minHeight = AppTheme.sizes.default),
            onClick = { onAction(ConnectionsAction.SelectEndpoint(endpoint)) },
            enabled = status.isActive,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier
                        .padding(AppTheme.spaces.large)
                        .size(AppTheme.sizes.mediumSmall),
                    icon = if (selected) {
                        FlowIcons.Selected
                    } else {
                        FlowIcons.NotSelected
                    },
                    tint = if (status.isActive) {
                        AppTheme.colors.onSurface
                    } else {
                        AppTheme.colors.onSurface.copy(alpha = 0.37f)
                    },
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

private val EndpointStatus.isActive: Boolean
    get() = this == EndpointStatus.Active
