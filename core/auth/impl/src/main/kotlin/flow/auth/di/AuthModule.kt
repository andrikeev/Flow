package flow.auth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.auth.api.AuthService
import flow.auth.api.TokenProvider
import flow.auth.impl.AuthServiceImpl

@Module
@InstallIn(SingletonComponent::class)
internal interface AuthModule {
    @Binds
    fun authService(impl: AuthServiceImpl): AuthService

    @Binds
    fun tokenProvider(impl: AuthServiceImpl): TokenProvider
}
