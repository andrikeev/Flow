package flow.domain.usecase

import flow.data.api.repository.EndpointsRepository
import flow.data.api.repository.SettingsRepository
import flow.models.settings.Endpoint
import javax.inject.Inject

interface RemoveEndpointUseCase : suspend (Endpoint) -> Unit

internal class RemoveEndpointUseCaseImpl @Inject constructor(
    private val endpointsRepository: EndpointsRepository,
    private val settingsRepository: SettingsRepository,
) : RemoveEndpointUseCase {
    override suspend operator fun invoke(endpoint: Endpoint) {
        endpointsRepository.remove(endpoint)
        if (settingsRepository.getSettings().endpoint == endpoint) {
            settingsRepository.setEndpoint(Endpoint.Proxy)
        }
    }
}
