package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.logger.api.LoggerFactory
import org.koin.core.context.GlobalContext
import javax.inject.Singleton

/**
 * Inverse-bridge: Koin owns the single [LoggerFactory] (loggerModule); remaining Hilt
 * consumers read it from Koin during the migration. Removed in Ф7.
 */
@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {
    @Provides
    @Singleton
    fun loggerFactory(): LoggerFactory = GlobalContext.get().get()
}
