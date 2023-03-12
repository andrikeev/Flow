package flow.domain.usecase

import flow.data.api.repository.SettingsRepository
import flow.models.settings.Theme
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(theme: Theme) {
        settingsRepository.setTheme(theme)
    }
}
