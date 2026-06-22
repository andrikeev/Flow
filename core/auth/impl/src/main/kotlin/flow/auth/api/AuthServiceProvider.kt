package flow.auth.api

import flow.auth.impl.AuthServiceImpl
import flow.network.api.NetworkApi
import flow.securestorage.PreferencesStorage

/**
 * Framework-agnostic factory for the auth service. The returned instance implements
 * both [AuthService] and [TokenProvider] and is stateful, so callers must keep it as a
 * single (singleton) instance and expose both interfaces from it.
 *
 * Used by the Koin module and by the Hilt bridge in the Android app.
 */
fun createAuthService(
    api: NetworkApi,
    preferencesStorage: PreferencesStorage,
): AuthService = AuthServiceImpl(api, preferencesStorage)
