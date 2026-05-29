package flow.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.designsystem.component.Body
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Surface
import flow.designsystem.theme.AppTheme
import flow.domain.model.endpoint.EndpointStatus
import flow.domain.usecase.ObserveEndpointStatusUseCase
import flow.domain.usecase.ObserveSettingsUseCase
import flow.domain.usecase.SetProxyUseCase
import flow.models.settings.Proxy
import flow.navigation.viewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ConnectionViewModel @Inject constructor(
    observeEndpointStatusUseCase: ObserveEndpointStatusUseCase,
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val setProxyUseCase: SetProxyUseCase,
) : ViewModel() {
    val status: StateFlow<EndpointStatus> = observeEndpointStatusUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EndpointStatus.Updating,
        )

    val proxy: StateFlow<Proxy> = observeSettingsUseCase()
        .map { it.proxy }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Proxy(),
        )

    fun setProxy(proxy: Proxy) {
        viewModelScope.launch {
            setProxyUseCase(proxy)
        }
    }
}

@Composable
fun ConnectionItem() {
    val viewModel: ConnectionViewModel = viewModel()
    val status by viewModel.status.collectAsState()
    val proxy by viewModel.proxy.collectAsState()
    var showProxyDialog by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.sizes.extraLarge),
        onClick = { showProxyDialog = true },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = AppTheme.spaces.large),
            ) {
                BodyLarge(stringResource(R.string.connection_item_title))
                Body(
                    text = if (proxy.enabled && proxy.host.isNotBlank()) {
                        stringResource(
                            R.string.connection_proxy_subtitle,
                            "${proxy.host}:${proxy.port}",
                        )
                    } else {
                        "rutracker.org"
                    },
                    color = AppTheme.colors.outline,
                )
            }
            ConnectionStatusIcon(status)
        }
    }
    if (showProxyDialog) {
        ProxySettingsDialog(
            proxy = proxy,
            onConfirm = { newProxy ->
                viewModel.setProxy(newProxy)
                showProxyDialog = false
            },
            onDismiss = { showProxyDialog = false },
        )
    }
}
