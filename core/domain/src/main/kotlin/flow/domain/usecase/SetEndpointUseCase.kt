package flow.domain.usecase

import flow.data.api.repository.SettingsRepository
import flow.models.settings.Endpoint
import javax.inject.Inject

class SetEndpointUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(endpoint: Endpoint) {
        settingsRepository.setEndpoint(endpoint)
    }
}
