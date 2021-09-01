package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.entity.settings.SyncPeriod
import me.rutrackersearch.domain.repository.SettingsRepository
import me.rutrackersearch.domain.service.FavoritesSyncService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetFavoritesSyncPeriodUseCase @Inject constructor(
    private val repository: SettingsRepository,
    private val syncService: FavoritesSyncService
) {
    suspend operator fun invoke(syncPeriod: SyncPeriod) {
        repository.setFavoritesSyncPeriod(syncPeriod)
        syncService.setSyncPeriod(syncPeriod)
    }
}
