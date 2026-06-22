package flow.domain.usecase

import flow.data.api.repository.SettingsRepository
import flow.models.settings.Settings
import kotlinx.coroutines.flow.Flow

class ObserveSettingsUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<Settings> = settingsRepository.observeSettings()
}
