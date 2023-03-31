package flow.testing.repository

import flow.data.api.repository.SettingsRepository
import flow.models.settings.Endpoint
import flow.models.settings.Settings
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestSettingsRepository : SettingsRepository {
    private val mutableSettings = MutableStateFlow(Settings())

    override suspend fun getSettings(): Settings {
        return mutableSettings.value
    }

    override fun observeSettings(): Flow<Settings> = mutableSettings.asStateFlow()

    override suspend fun setTheme(theme: Theme) {
        val settings = mutableSettings.value.copy(theme = theme)
        mutableSettings.emit(settings)
    }

    override suspend fun setEndpoint(endpoint: Endpoint) {
        val settings = mutableSettings.value.copy(endpoint = endpoint)
        mutableSettings.emit(settings)
    }

    override suspend fun setFavoritesSyncPeriod(syncPeriod: SyncPeriod) {
        val settings = mutableSettings.value.copy(favoritesSyncPeriod = syncPeriod)
        mutableSettings.emit(settings)
    }

    override suspend fun setBookmarksSyncPeriod(syncPeriod: SyncPeriod) {
        val settings = mutableSettings.value.copy(bookmarksSyncPeriod = syncPeriod)
        mutableSettings.emit(settings)
    }
}
