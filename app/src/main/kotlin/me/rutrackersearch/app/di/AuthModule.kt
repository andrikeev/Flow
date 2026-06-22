package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.auth.api.AuthService
import flow.auth.api.TokenProvider
import flow.auth.api.createAuthService
import flow.network.api.NetworkApi
import flow.securestorage.PreferencesStorage
import javax.inject.Singleton

/**
 * Bridges the framework-agnostic auth service into the Android Hilt graph.
 *
 * core:auth:impl no longer depends on Hilt (it exposes a Koin module instead).
 * A single stateful instance backs both [AuthService] and [TokenProvider], matching
 * the previous @Singleton behaviour.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun authService(
        api: NetworkApi,
        preferencesStorage: PreferencesStorage,
    ): AuthService = createAuthService(api, preferencesStorage)

    @Provides
    @Singleton
    fun tokenProvider(authService: AuthService): TokenProvider = authService as TokenProvider
}
