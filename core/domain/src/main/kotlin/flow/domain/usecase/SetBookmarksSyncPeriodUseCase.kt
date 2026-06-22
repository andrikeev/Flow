package flow.domain.usecase

import flow.data.api.repository.SettingsRepository
import flow.dispatchers.api.Dispatchers
import flow.models.settings.SyncPeriod
import flow.work.api.BackgroundService
import kotlinx.coroutines.withContext

class SetBookmarksSyncPeriodUseCase(
    private val settingsRepository: SettingsRepository,
    private val backgroundService: BackgroundService,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(syncPeriod: SyncPeriod) {
        withContext(dispatchers.default) {
            settingsRepository.setBookmarksSyncPeriod(syncPeriod)
            backgroundService.syncBookmarks(syncPeriod)
        }
    }
}
