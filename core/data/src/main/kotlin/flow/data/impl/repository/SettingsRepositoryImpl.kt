package flow.data.impl.repository

import flow.data.api.repository.SettingsRepository
import flow.models.settings.Settings
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme
import flow.securestorage.SecureStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val secureStorage: SecureStorage,
) : SettingsRepository {
    private val mutableSettings = MutableStateFlow(secureStorage.getSettings())

    override fun observeSettings(): Flow<Settings> = mutableSettings.asStateFlow()

    override suspend fun setTheme(theme: Theme) {
        val settings = mutableSettings.value.copy(theme = theme)
        secureStorage.saveSettings(settings)
        mutableSettings.emit(settings)
    }

    override suspend fun setFavoritesSyncPeriod(syncPeriod: SyncPeriod) {
        val settings = mutableSettings.value.copy(favoritesSyncPeriod = syncPeriod)
        secureStorage.saveSettings(settings)
        mutableSettings.emit(settings)
    }

    override suspend fun setBookmarksSyncPeriod(syncPeriod: SyncPeriod) {
        val settings = mutableSettings.value.copy(bookmarksSyncPeriod = syncPeriod)
        secureStorage.saveSettings(settings)
        mutableSettings.emit(settings)
    }
}
