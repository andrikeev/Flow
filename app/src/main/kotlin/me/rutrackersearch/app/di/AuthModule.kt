package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.auth.api.AuthService
import flow.auth.api.TokenProvider
import org.koin.core.context.GlobalContext
import javax.inject.Singleton

/**
 * Inverse-bridge: Koin owns the auth service (authModule); remaining Hilt consumers
 * (data services, domain) read AuthService/TokenProvider from Koin during the migration.
 * A single stateful instance backs both interfaces (see authModule).
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun authService(): AuthService = GlobalContext.get().get()

    @Provides
    @Singleton
    fun tokenProvider(): TokenProvider = GlobalContext.get().get()
}
