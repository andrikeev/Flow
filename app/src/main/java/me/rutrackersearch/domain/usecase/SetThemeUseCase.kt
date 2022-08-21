package me.rutrackersearch.domain.usecase

import me.rutrackersearch.models.settings.Theme
import me.rutrackersearch.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(theme: Theme) {
        settingsRepository.setTheme(theme)
    }
}
