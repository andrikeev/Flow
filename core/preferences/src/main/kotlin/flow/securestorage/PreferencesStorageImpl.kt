package flow.securestorage

import android.content.SharedPreferences
import flow.dispatchers.api.Dispatchers
import flow.models.settings.Endpoint
import flow.models.settings.Settings
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme
import flow.securestorage.model.Account
import flow.securestorage.model.EndpointConverter
import flow.securestorage.preferences.SharedPreferencesFactory
import flow.securestorage.utils.clear
import flow.securestorage.utils.edit
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PreferencesStorageImpl @Inject constructor(
    sharedPreferencesFactory: SharedPreferencesFactory,
    private val dispatchers: Dispatchers,
) : PreferencesStorage {
    private val accountPreferences: SharedPreferences by lazy {
        sharedPreferencesFactory.getSharedPreferences("account")
    }
    private val settingsPreferences: SharedPreferences by lazy {
        sharedPreferencesFactory.getSharedPreferences("settings")
    }
    private val ratingPreferences: SharedPreferences by lazy {
        sharedPreferencesFactory.getSharedPreferences("rating")
    }

    override suspend fun saveAccount(account: Account) {
        withContext(dispatchers.io) {
            accountPreferences.edit {
                putString(accountIdKey, account.id)
                putString(accountUsernameKey, account.name)
                putString(accountPasswordKey, account.password)
                putString(accountTokenKey, account.token)
                putString(accountAvatarKey, account.avatarUrl)
            }
        }
    }

    override suspend fun getAccount(): Account? {
        return withContext(dispatchers.io) {
            runCatching {
                val id = accountPreferences.getString(accountIdKey, null)
                val username = accountPreferences.getString(accountUsernameKey, null)
                val token = accountPreferences.getString(accountTokenKey, null)
                val password = accountPreferences.getString(accountPasswordKey, null)
                if (id != null && username != null && token != null && password != null) {
                    Account(
                        id = id,
                        name = username,
                        token = token,
                        password = password,
                        avatarUrl = accountPreferences.getString(accountAvatarKey, null),
                    )
                } else {
                    null
                }
            }.getOrNull()
        }
    }

    override suspend fun clearAccount() {
        withContext(dispatchers.io) {
            accountPreferences.clear()
        }
    }

    override suspend fun saveSettings(settings: Settings) {
        withContext(dispatchers.io) {
            settingsPreferences.edit {
                putString(endpointKey, with(EndpointConverter) { settings.endpoint.toJson() })
                putString(themeKey, settings.theme.name)
                putString(favoritesSyncPeriodKey, settings.favoritesSyncPeriod.name)
                putString(bookmarksSyncPeriodKey, settings.bookmarksSyncPeriod.name)
            }
        }
    }

    override suspend fun getSettings(): Settings {
        return withContext(dispatchers.io) {
            val endpoint = settingsPreferences.getString(endpointKey, null)?.let {
                with(EndpointConverter) { fromJson(it) }
            } ?: Endpoint.Proxy
            val theme = settingsPreferences.getString(themeKey, null)?.let {
                enumValueOf(it)
            } ?: Theme.SYSTEM
            val favoritesSyncPeriod =
                settingsPreferences.getString(favoritesSyncPeriodKey, null)?.let {
                    enumValueOf(it)
                } ?: SyncPeriod.OFF
            val bookmarksSyncPeriod =
                settingsPreferences.getString(bookmarksSyncPeriodKey, null)?.let {
                    enumValueOf(it)
                } ?: SyncPeriod.OFF
            Settings(
                endpoint = endpoint,
                theme = theme,
                favoritesSyncPeriod = favoritesSyncPeriod,
                bookmarksSyncPeriod = bookmarksSyncPeriod,
            )
        }
    }

    override suspend fun getRatingLaunchCount(): Int {
        return withContext(dispatchers.io) {
            ratingPreferences.getInt(ratingLaunchCountKey, 0)
        }
    }

    override suspend fun setRatingLaunchCount(count: Int) {
        withContext(dispatchers.io) {
            ratingPreferences.edit { putInt(ratingLaunchCountKey, count) }
        }
    }

    override suspend fun getRatingDisabled(): Boolean {
        return withContext(dispatchers.io) {
            ratingPreferences.getBoolean(ratingDisabledKey, false)
        }
    }

    override suspend fun setRatingDisabled(value: Boolean) {
        withContext(dispatchers.io) {
            ratingPreferences.edit { putBoolean(ratingDisabledKey, value) }
        }
    }

    override suspend fun getRatingPostponed(): Boolean {
        return withContext(dispatchers.io) {
            ratingPreferences.getBoolean(ratingPostponedKey, false)
        }
    }

    override suspend fun setRatingPostponed(value: Boolean) {
        withContext(dispatchers.io) {
            ratingPreferences.edit { putBoolean(ratingPostponedKey, value) }
        }
    }

    private companion object {
        const val accountIdKey = "account_id"
        const val accountUsernameKey = "account_username"
        const val accountPasswordKey = "account_password"
        const val accountTokenKey = "account_token"
        const val accountAvatarKey = "account_avatar_url"

        const val endpointKey = "endpoint"
        const val themeKey = "theme"
        const val favoritesSyncPeriodKey = "favorites_sync_period"
        const val bookmarksSyncPeriodKey = "bookmarks_sync_period"

        const val ratingLaunchCountKey = "rating_launch_count"
        const val ratingDisabledKey = "rating_disabled"
        const val ratingPostponedKey = "rating_postponed"
    }
}
