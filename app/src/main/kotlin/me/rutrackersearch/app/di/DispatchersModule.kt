package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.dispatchers.api.Dispatchers
import flow.dispatchers.api.createDispatchers
import javax.inject.Singleton

/**
 * Bridges the framework-agnostic [Dispatchers] into the Android Hilt graph.
 *
 * core:dispatchers no longer depends on Hilt (it exposes a Koin module instead);
 * this bridge keeps existing Hilt consumers working until the app moves to Koin.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Singleton
    fun dispatchers(): Dispatchers = createDispatchers()
}
