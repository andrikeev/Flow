package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.data.api.service.ConnectionService
import flow.data.api.service.StoreService
import org.koin.core.context.GlobalContext
import javax.inject.Singleton

/**
 * Inverse-bridge: Koin owns the Context-only data services (serviceModule); remaining
 * Hilt consumers (domain use cases) read them from Koin during the migration.
 */
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides @Singleton fun connectionService(): ConnectionService = GlobalContext.get().get()

    @Provides @Singleton fun storeService(): StoreService = GlobalContext.get().get()
}
