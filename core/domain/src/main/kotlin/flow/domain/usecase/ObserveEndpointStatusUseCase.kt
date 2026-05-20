package flow.domain.usecase

import flow.data.api.service.ConnectionService
import flow.domain.model.endpoint.EndpointStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

interface ObserveEndpointStatusUseCase : () -> Flow<EndpointStatus>

internal class ObserveEndpointStatusUseCaseImpl @Inject constructor(
    private val connectionService: ConnectionService,
) : ObserveEndpointStatusUseCase {
    override fun invoke(): Flow<EndpointStatus> {
        return connectionService.networkUpdates
            .transformLatest { isOnline ->
                emit(EndpointStatus.Updating)
                emit(
                    when {
                        !isOnline -> EndpointStatus.NoInternet
                        connectionService.isReachable("rutracker.org") -> EndpointStatus.Active
                        connectionService.isReachable("google.com") -> EndpointStatus.Blocked
                        else -> EndpointStatus.NoInternet
                    },
                )
            }
            .onStart { emit(EndpointStatus.Updating) }
    }
}
