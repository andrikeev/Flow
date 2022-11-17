package flow.domain.usecase

import flow.data.api.SettingsRepository
import flow.models.settings.SyncPeriod
import flow.work.api.BackgroundService
import javax.inject.Inject

class SetFavoritesSyncPeriodUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val backgroundService: BackgroundService,
) {
    suspend operator fun invoke(syncPeriod: SyncPeriod) {
        settingsRepository.setFavoritesSyncPeriod(syncPeriod)
        backgroundService.syncFavorites(syncPeriod)
    }
}
