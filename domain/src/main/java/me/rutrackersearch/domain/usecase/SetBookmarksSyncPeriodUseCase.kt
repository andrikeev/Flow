package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.entity.settings.SyncPeriod
import me.rutrackersearch.domain.repository.SettingsRepository
import me.rutrackersearch.domain.service.BookmarksSyncService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetBookmarksSyncPeriodUseCase @Inject constructor(
    private val repository: SettingsRepository,
    private val syncService: BookmarksSyncService
) {
    suspend operator fun invoke(syncPeriod: SyncPeriod) {
        repository.setBookmarksSyncPeriod(syncPeriod)
        syncService.setSyncPeriod(syncPeriod)
    }
}
