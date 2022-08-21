package me.rutrackersearch.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.models.settings.Settings
import me.rutrackersearch.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<Settings> = settingsRepository.observeSettings()
}
