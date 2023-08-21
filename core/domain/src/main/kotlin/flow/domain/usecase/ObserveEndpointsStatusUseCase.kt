package flow.domain.usecase

import flow.data.api.repository.EndpointsRepository
import flow.domain.model.endpoint.EndpointState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

interface ObserveEndpointsStatusUseCase : suspend () -> Flow<List<EndpointState>>

internal class ObserveEndpointsStatusUseCaseImpl @Inject constructor(
    private val endpointsRepository: EndpointsRepository,
    private val observeEndpointStatusUseCase: ObserveEndpointStatusUseCase,
) : ObserveEndpointsStatusUseCase {
    override suspend fun invoke(): Flow<List<EndpointState>> {
        return endpointsRepository.observeAll()
            .flatMapLatest { endpoints ->
                combine(
                    flows = endpoints.map { endpoint -> observeEndpointStatusUseCase(endpoint) },
                    transform = Array<EndpointState>::toList,
                )
            }
    }
}
