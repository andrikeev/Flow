package flow.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import flow.designsystem.component.Body
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.ModalBottomSheet
import flow.designsystem.component.Surface
import flow.designsystem.component.rememberVisibilityState
import flow.designsystem.theme.AppTheme
import flow.domain.model.endpoint.EndpointState
import flow.navigation.viewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ConnectionItem() = ConnectionItem(
    viewModel = viewModel(),
)

@Composable
private fun ConnectionItem(viewModel: ConnectionsViewModel) {
    val dialogState = rememberVisibilityState()
    if (dialogState.visible) {
        ModalBottomSheet(
            visible = dialogState.visible,
            onDismissRequest = dialogState::hide,
            content = { ConnectionsList() },
        )
    }
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ConnectionsSideEffect.ShowConnectionDialog -> dialogState.show()
        }
    }
    val state by viewModel.collectAsState()
    ConnectionItem(
        state = state,
        onAction = viewModel::perform,
    )
}

@Composable
private fun ConnectionItem(
    state: Collection<EndpointState>,
    onAction: (ConnectionsAction) -> Unit,
) {
    val selectedEndpointState by remember(state) {
        derivedStateOf { state.firstOrNull { it.selected } }
    }
    selectedEndpointState?.let { endpointState ->
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.sizes.extraLarge),
            onClick = { onAction(ConnectionsAction.ClickConnectionItem) },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = AppTheme.spaces.large),
                ) {
                    BodyLarge(stringResource(R.string.connection_item_title))
                    Body(
                        text = endpointState.endpoint.host,
                        color = AppTheme.colors.outline,
                    )
                }
                ConnectionStatusIcon(endpointState.status)
            }
        }
    }
}
