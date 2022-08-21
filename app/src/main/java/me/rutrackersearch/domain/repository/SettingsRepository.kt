package me.rutrackersearch.domain.repository

import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.models.settings.Settings
import me.rutrackersearch.models.settings.SyncPeriod
import me.rutrackersearch.models.settings.Theme

interface SettingsRepository {
    fun observeSettings(): Flow<Settings>
    suspend fun setTheme(theme: Theme)
    suspend fun setFavoritesSyncPeriod(syncPeriod: SyncPeriod)
    suspend fun setBookmarksSyncPeriod(syncPeriod: SyncPeriod)
}
