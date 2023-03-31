package flow.data.api.repository

import flow.models.settings.Endpoint
import flow.models.settings.Settings
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getSettings(): Settings
    fun observeSettings(): Flow<Settings>
    suspend fun setTheme(theme: Theme)
    suspend fun setEndpoint(endpoint: Endpoint)
    suspend fun setFavoritesSyncPeriod(syncPeriod: SyncPeriod)
    suspend fun setBookmarksSyncPeriod(syncPeriod: SyncPeriod)
}
