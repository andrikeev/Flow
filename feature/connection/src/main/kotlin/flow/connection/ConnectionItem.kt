package flow.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.designsystem.component.Body
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Surface
import flow.designsystem.theme.AppTheme
import flow.domain.model.endpoint.EndpointStatus
import flow.domain.usecase.ObserveEndpointStatusUseCase
import flow.navigation.viewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class ConnectionStatusViewModel @Inject constructor(
    observeEndpointStatusUseCase: ObserveEndpointStatusUseCase,
) : ViewModel() {
    val status: StateFlow<EndpointStatus> = observeEndpointStatusUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EndpointStatus.Updating,
        )
}

@Composable
fun ConnectionItem() {
    val viewModel: ConnectionStatusViewModel = viewModel()
    val status by viewModel.status.collectAsState()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.sizes.extraLarge),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = AppTheme.spaces.large),
            ) {
                BodyLarge(stringResource(R.string.connection_item_title))
                Body(
                    text = "rutracker.org",
                    color = AppTheme.colors.outline,
                )
            }
            ConnectionStatusIcon(status)
        }
    }
}
