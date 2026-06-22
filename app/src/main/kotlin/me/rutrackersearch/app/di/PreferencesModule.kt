package me.rutrackersearch.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import flow.dispatchers.api.Dispatchers
import flow.securestorage.PreferencesStorage
import flow.securestorage.createPreferencesStorage
import javax.inject.Singleton

/**
 * Bridges the framework-agnostic [PreferencesStorage] into the Android Hilt graph.
 *
 * core:preferences no longer depends on Hilt; the encrypted SharedPreferences factory
 * is wired inside createPreferencesStorage(). Matches the previous @Singleton behaviour.
 */
@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun preferencesStorage(
        @ApplicationContext context: Context,
        dispatchers: Dispatchers,
    ): PreferencesStorage = createPreferencesStorage(context, dispatchers)
}
