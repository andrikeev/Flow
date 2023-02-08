package flow.domain.usecase

import flow.data.api.repository.SettingsRepository
import flow.models.settings.SyncPeriod
import flow.work.api.BackgroundService
import javax.inject.Inject

class SetBookmarksSyncPeriodUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val backgroundService: BackgroundService,
) {
    suspend operator fun invoke(syncPeriod: SyncPeriod) {
        settingsRepository.setBookmarksSyncPeriod(syncPeriod)
        backgroundService.syncBookmarks(syncPeriod)
    }
}
