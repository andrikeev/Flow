package flow.domain.usecase

import flow.data.api.repository.SettingsRepository
import flow.models.settings.Proxy
import javax.inject.Inject

class SetProxyUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(proxy: Proxy) {
        settingsRepository.setProxy(proxy)
    }
}
