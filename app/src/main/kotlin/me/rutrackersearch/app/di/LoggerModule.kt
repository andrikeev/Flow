package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.logger.api.LoggerFactory
import flow.logger.api.createLoggerFactory
import javax.inject.Singleton

/**
 * Bridges the framework-agnostic [LoggerFactory] into the Android Hilt graph.
 *
 * core:logger no longer depends on Hilt (it exposes a Koin module instead);
 * this bridge keeps existing Hilt consumers working until the app moves to Koin.
 */
@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {
    @Provides
    @Singleton
    fun loggerFactory(): LoggerFactory = createLoggerFactory()
}
