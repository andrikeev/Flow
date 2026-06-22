package flow.securestorage.di

import flow.securestorage.PreferencesStorage
import flow.securestorage.createPreferencesStorage
import org.koin.dsl.module

/**
 * Koin module for [PreferencesStorage]. Context and Dispatchers are resolved from the
 * Koin graph (androidContext() + dispatchersModule). On Android the same instance is
 * exposed to remaining Hilt consumers via an inverse-bridge in :app.
 */
val preferencesModule = module {
    single<PreferencesStorage> { createPreferencesStorage(get(), get()) }
}
