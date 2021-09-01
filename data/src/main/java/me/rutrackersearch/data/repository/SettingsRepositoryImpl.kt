package me.rutrackersearch.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import me.rutrackersearch.data.security.SecureStorageFactory
import me.rutrackersearch.data.utils.edit
import me.rutrackersearch.domain.entity.settings.Settings
import me.rutrackersearch.domain.entity.settings.SyncPeriod
import me.rutrackersearch.domain.entity.settings.Theme
import me.rutrackersearch.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    secureStorageFactory: SecureStorageFactory,
) : SettingsRepository {
    private val preferences = secureStorageFactory.getSharedPreferences("settings")
    private val mutableSettings = MutableStateFlow(readSettings())

    override fun observeSettings(): Flow<Settings> = mutableSettings

    override suspend fun setTheme(theme: Theme) {
        mutableSettings.emit(mutableSettings.value.copy(theme = theme))
        preferences.edit {
            putString(themeKey, theme.name)
        }
    }

    override suspend fun setFavoritesSyncPeriod(syncPeriod: SyncPeriod) {
        mutableSettings.emit(mutableSettings.value.copy(favoritesSyncPeriod = syncPeriod))
        preferences.edit {
            putString(favoritesSyncPeriodKey, syncPeriod.name)
        }
    }

    override suspend fun setBookmarksSyncPeriod(syncPeriod: SyncPeriod) {
        mutableSettings.emit(mutableSettings.value.copy(bookmarksSyncPeriod = syncPeriod))
        preferences.edit {
            putString(bookmarksSyncPeriodKey, syncPeriod.name)
        }
    }

    private fun readSettings(): Settings {
        val theme = preferences.getString(themeKey, null)?.let {
            enumValueOf(it)
        } ?: Theme.SYSTEM
        val favoritesSyncPeriod = preferences.getString(favoritesSyncPeriodKey, null)?.let {
            enumValueOf(it)
        } ?: SyncPeriod.OFF
        val bookmarksSyncPeriod = preferences.getString(bookmarksSyncPeriodKey, null)?.let {
            enumValueOf(it)
        } ?: SyncPeriod.OFF
        return Settings(theme, favoritesSyncPeriod, bookmarksSyncPeriod)
    }

    private companion object {
        const val themeKey = "theme"
        const val favoritesSyncPeriodKey = "favorites_sync_period"
        const val bookmarksSyncPeriodKey = "bookmarks_sync_period"
    }
}
