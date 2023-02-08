package flow.domain.usecase

import flow.data.api.repository.SettingsRepository
import flow.models.settings.Settings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<Settings> = settingsRepository.observeSettings()
}
