package flow.data.impl.repository

import flow.common.SingleItemMutableSharedFlow
import flow.data.api.repository.SettingsRepository
import flow.models.settings.Endpoint
import flow.models.settings.Settings
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme
import flow.securestorage.PreferencesStorage
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage,
) : SettingsRepository {
    private val mutableSettings = SingleItemMutableSharedFlow<Settings>()

    override suspend fun getSettings() = preferencesStorage.getSettings()

    override fun observeSettings() = mutableSettings
        .asSharedFlow()
        .onStart { emit(getSettings()) }

    override suspend fun setTheme(theme: Theme) {
        updateSettings { copy(theme = theme) }
    }

    override suspend fun setEndpoint(endpoint: Endpoint) {
        updateSettings { copy(endpoint = endpoint) }
    }

    override suspend fun setFavoritesSyncPeriod(syncPeriod: SyncPeriod) {
        updateSettings { copy(favoritesSyncPeriod = syncPeriod) }
    }

    override suspend fun setBookmarksSyncPeriod(syncPeriod: SyncPeriod) {
        updateSettings { copy(bookmarksSyncPeriod = syncPeriod) }
    }

    private suspend fun updateSettings(update: Settings.() -> Settings) {
        val settings = preferencesStorage
            .getSettings()
            .let(update)
        preferencesStorage.saveSettings(settings)
        mutableSettings.emit(settings)
    }
}
