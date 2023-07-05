package flow.domain.usecase

import flow.data.api.repository.SettingsRepository
import flow.data.api.service.ConnectionService
import flow.domain.model.endpoint.EndpointState
import flow.domain.model.endpoint.EndpointStatus
import flow.models.settings.Endpoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

interface ObserveEndpointStatusUseCase : (Endpoint) -> Flow<EndpointState>

internal class ObserveEndpointStatusUseCaseImpl @Inject constructor(
    private val connectionService: ConnectionService,
    private val settingsRepository: SettingsRepository,
) : ObserveEndpointStatusUseCase {
    override fun invoke(endpoint: Endpoint): Flow<EndpointState> {
        return connectionService
            .networkUpdates
            .transformLatest { isOnline ->
                val state = initialState(endpoint)
                emit(state)
                val status = when {
                    !isOnline -> EndpointStatus.NoInternet
                    endpoint.isReachable() -> EndpointStatus.Active
                    isInternetReachable() -> EndpointStatus.Blocked
                    else -> EndpointStatus.NoInternet
                }
                observeSelectedEndpoint()
                    .collectLatest { selected ->
                        emit(
                            state.copy(
                                selected = endpoint == selected,
                                status = status,
                            ),
                        )
                    }
            }
            .onStart { emit(initialState(endpoint)) }
    }

    private suspend fun isInternetReachable() = connectionService.isReachable("google.com")

    private suspend fun Endpoint.isReachable() = connectionService.isReachable(host)

    private suspend fun initialState(endpoint: Endpoint) = EndpointState(
        endpoint = endpoint,
        selected = endpoint == settingsRepository.getSettings().endpoint,
        status = EndpointStatus.Updating,
    )

    private fun observeSelectedEndpoint() = settingsRepository.observeSettings().map { it.endpoint }
}
