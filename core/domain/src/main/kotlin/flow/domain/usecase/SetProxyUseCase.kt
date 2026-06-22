package flow.domain.usecase

import flow.data.api.repository.SettingsRepository
import flow.models.settings.Proxy

class SetProxyUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(proxy: Proxy) {
        settingsRepository.setProxy(proxy)
    }
}
