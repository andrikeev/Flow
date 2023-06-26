package flow.domain.usecase

import flow.data.api.repository.EndpointsRepository
import flow.models.settings.Endpoint
import javax.inject.Inject

interface AddEndpointUseCase : suspend (String) -> Unit

internal class AddEndpointUseCaseImpl @Inject constructor(
    private val endpointsRepository: EndpointsRepository,
) : AddEndpointUseCase {
    override suspend operator fun invoke(endpoint: String) {
        endpointsRepository.add(Endpoint.Mirror(endpoint))
    }
}
