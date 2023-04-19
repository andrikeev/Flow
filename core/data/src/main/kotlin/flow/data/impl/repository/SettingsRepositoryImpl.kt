package flow.data.impl.repository

import flow.data.api.repository.SettingsRepository
import flow.dispatchers.api.Dispatchers
import flow.models.settings.Endpoint
import flow.models.settings.Settings
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme
import flow.securestorage.SecureStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val secureStorage: SecureStorage,
    private val dispatchers: Dispatchers,
) : SettingsRepository {
    private val mutableSettings = MutableStateFlow(secureStorage.getSettings())

    override suspend fun getSettings(): Settings = mutableSettings.value

    override fun observeSettings(): Flow<Settings> = mutableSettings.asSharedFlow()

    override suspend fun setTheme(theme: Theme) {
        withContext(dispatchers.io) {
            val settings = secureStorage.getSettings().copy(theme = theme)
            secureStorage.saveSettings(settings)
            mutableSettings.emit(settings)
        }
    }

    override suspend fun setEndpoint(endpoint: Endpoint) {
        withContext(dispatchers.io) {
            val settings = secureStorage.getSettings().copy(endpoint = endpoint)
            secureStorage.saveSettings(settings)
            mutableSettings.emit(settings)
        }
    }

    override suspend fun setFavoritesSyncPeriod(syncPeriod: SyncPeriod) {
        withContext(dispatchers.io) {
            val settings = secureStorage.getSettings().copy(favoritesSyncPeriod = syncPeriod)
            secureStorage.saveSettings(settings)
            mutableSettings.emit(settings)
        }
    }

    override suspend fun setBookmarksSyncPeriod(syncPeriod: SyncPeriod) {
        withContext(dispatchers.io) {
            val settings = secureStorage.getSettings().copy(bookmarksSyncPeriod = syncPeriod)
            secureStorage.saveSettings(settings)
            mutableSettings.emit(settings)
        }
    }
}
