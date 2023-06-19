package flow.domain.usecase

import flow.domain.model.endpoint.EndpointState
import flow.models.settings.Endpoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

interface ObserveEndpointsStatusUseCase : () -> Flow<List<EndpointState>>

internal class ObserveEndpointsStatusUseCaseImpl @Inject constructor(
    private val observeEndpointStatusUseCase: ObserveEndpointStatusUseCase,
) : ObserveEndpointsStatusUseCase {
    override fun invoke(): Flow<List<EndpointState>> {
        return combine<EndpointState, List<EndpointState>>(
            flows = Endpoint.values().map { endpoint -> observeEndpointStatusUseCase(endpoint) },
            transform = Array<EndpointState>::toList,
        )
    }
}
