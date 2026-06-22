package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.dispatchers.api.Dispatchers
import org.koin.core.context.GlobalContext
import javax.inject.Singleton

/**
 * Inverse-bridge: Koin owns the single [Dispatchers] instance (dispatchersModule,
 * registered in FlowApplication); remaining Hilt consumers read it from Koin during the
 * migration. Removed in Ф7 once the app runs entirely on Koin.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Singleton
    fun dispatchers(): Dispatchers = GlobalContext.get().get()
}
