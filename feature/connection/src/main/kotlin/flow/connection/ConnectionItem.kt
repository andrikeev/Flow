package flow.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import flow.designsystem.component.Body
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Surface
import flow.designsystem.theme.AppTheme
import flow.navigation.viewModel
import flow.ui.component.ModalBottomDialog
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ConnectionItem() = ConnectionItem(
    viewModel = viewModel(),
)

@Composable
private fun ConnectionItem(viewModel: ConnectionsViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ConnectionsSideEffect.ShowConnectionDialog -> {
                showDialog = true
            }
        }
    }
    if (showDialog) {
        ModalBottomDialog(
            onDismissRequest = { showDialog = false },
            content = { ConnectionsList() },
        )
    }
    val state by viewModel.collectAsState()
    ConnectionItem(
        state = state,
        onAction = viewModel::perform,
    )
}

@Composable
private fun ConnectionItem(
    state: ConnectionsState,
    onAction: (ConnectionsAction) -> Unit,
) {
    state.selected?.let { endpointState ->
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.sizes.extraLarge),
            onClick = { onAction(ConnectionsAction.ConnectionItemClick) },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = AppTheme.spaces.large),
                ) {
                    BodyLarge(stringResource(R.string.connection_item_title))
                    Body(
                        text = endpointState.endpoint.title,
                        color = AppTheme.colors.outline,
                    )
                }
                ConnectionStatusIcon(endpointState.status)
            }
        }
    }
}
