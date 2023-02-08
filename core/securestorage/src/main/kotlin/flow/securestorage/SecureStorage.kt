package flow.securestorage

import flow.models.settings.Settings
import flow.securestorage.model.Account

/**
 * Storage for secure data management.
 */
interface SecureStorage {
    /**
     * Save user account.
     */
    fun saveAccount(account: Account)

    /**
     * Load user account or null
     */
    fun getAccount(): Account?

    /**
     * Clear user account.
     */
    fun clearAccount()

    /**
     * Save user settings.
     */
    fun saveSettings(settings: Settings)

    /**
     * Load user setting or null.
     */
    fun getSettings(): Settings
}
