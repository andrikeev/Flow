package flow.securestorage

import android.content.Context
import flow.dispatchers.api.Dispatchers
import flow.securestorage.preferences.SharedPreferencesFactoryImpl

/**
 * Framework-agnostic factory for [PreferencesStorage]. Wires the internal encrypted
 * SharedPreferences factory, so the storage stays free of any DI framework.
 *
 * Used by the Hilt bridge in the Android app. This module is Android-specific
 * (EncryptedSharedPreferences); a multiplatform implementation (DataStore/Keychain)
 * and a Koin module will arrive when it is converted to KMP.
 */
fun createPreferencesStorage(
    context: Context,
    dispatchers: Dispatchers,
): PreferencesStorage = PreferencesStorageImpl(
    sharedPreferencesFactory = SharedPreferencesFactoryImpl(context),
    dispatchers = dispatchers,
)
