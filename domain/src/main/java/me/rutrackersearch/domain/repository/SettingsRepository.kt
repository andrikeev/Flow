package me.rutrackersearch.domain.repository

import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.domain.entity.settings.Settings
import me.rutrackersearch.domain.entity.settings.SyncPeriod
import me.rutrackersearch.domain.entity.settings.Theme

interface SettingsRepository {
    fun observeSettings(): Flow<Settings>
    suspend fun setTheme(theme: Theme)
    suspend fun setFavoritesSyncPeriod(syncPeriod: SyncPeriod)
    suspend fun setBookmarksSyncPeriod(syncPeriod: SyncPeriod)
}
