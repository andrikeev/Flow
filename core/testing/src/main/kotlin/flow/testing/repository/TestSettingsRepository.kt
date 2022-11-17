package flow.testing.repository

import flow.data.api.SettingsRepository
import flow.models.settings.Settings
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestSettingsRepository : SettingsRepository {
    private val mutableSettings = MutableStateFlow(Settings())

    override fun observeSettings(): Flow<Settings> = mutableSettings.asStateFlow()

    override suspend fun setTheme(theme: Theme) {
        val settings = mutableSettings.value.copy(theme = theme)
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
