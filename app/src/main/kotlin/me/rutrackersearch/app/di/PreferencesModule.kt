package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.securestorage.PreferencesStorage
import org.koin.core.context.GlobalContext
import javax.inject.Singleton

/**
 * Inverse-bridge: Koin owns the single [PreferencesStorage] (preferencesModule);
 * remaining Hilt consumers read it from Koin during the migration. Removed in Ф7.
 */
@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun preferencesStorage(): PreferencesStorage = GlobalContext.get().get()
}
