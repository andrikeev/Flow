package flow.securestorage

import flow.models.settings.Settings
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme
import flow.securestorage.model.Account
import flow.securestorage.preferences.SecurePreferencesFactory
import flow.securestorage.utils.clear
import flow.securestorage.utils.edit
import javax.inject.Inject

internal class SecurePreferencesStorage @Inject constructor(
    securePreferencesFactory: SecurePreferencesFactory,
) : SecureStorage {
    private val accountPreferences = securePreferencesFactory.getSharedPreferences("account")
    private val settingsPreferences = securePreferencesFactory.getSharedPreferences("settings")

    override fun saveAccount(account: Account) {
        accountPreferences.edit {
            putString(accountIdKey, account.id)
            putString(accountUsernameKey, account.name)
            putString(accountPasswordKey, account.password)
            putString(accountTokenKey, account.token)
            putString(accountAvatarKey, account.avatarUrl)
        }
    }

    override fun getAccount(): Account? {
        return kotlin.runCatching {
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

    override fun clearAccount() {
        accountPreferences.clear()
    }

    override fun saveSettings(settings: Settings) {
        settingsPreferences.edit {
            putString(themeKey, settings.theme.name)
            putString(favoritesSyncPeriodKey, settings.favoritesSyncPeriod.name)
            putString(bookmarksSyncPeriodKey, settings.bookmarksSyncPeriod.name)
        }
    }

    override fun getSettings(): Settings {
        val theme = settingsPreferences.getString(themeKey, null)?.let {
            enumValueOf(it)
        } ?: Theme.SYSTEM
        val favoritesSyncPeriod = settingsPreferences.getString(favoritesSyncPeriodKey, null)?.let {
            enumValueOf(it)
        } ?: SyncPeriod.OFF
        val bookmarksSyncPeriod = settingsPreferences.getString(bookmarksSyncPeriodKey, null)?.let {
            enumValueOf(it)
        } ?: SyncPeriod.OFF
        return Settings(theme, favoritesSyncPeriod, bookmarksSyncPeriod)
    }

    private companion object {
        const val accountIdKey = "account_id"
        const val accountUsernameKey = "account_username"
        const val accountPasswordKey = "account_password"
        const val accountTokenKey = "account_token"
        const val accountAvatarKey = "account_avatar_url"

        const val themeKey = "theme"
        const val favoritesSyncPeriodKey = "favorites_sync_period"
        const val bookmarksSyncPeriodKey = "bookmarks_sync_period"
    }
}
