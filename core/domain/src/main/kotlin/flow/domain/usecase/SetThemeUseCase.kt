package flow.domain.usecase

import flow.data.api.repository.SettingsRepository
import flow.models.settings.Theme

class SetThemeUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(theme: Theme) {
        settingsRepository.setTheme(theme)
    }
}
