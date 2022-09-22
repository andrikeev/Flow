package me.rutrackersearch.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.auth.AuthObservable
import me.rutrackersearch.auth.AuthService
import me.rutrackersearch.auth.AuthServiceImpl
import me.rutrackersearch.data.repository.AccountRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {
    @Binds
    @Singleton
    fun bindAuthService(impl: AuthServiceImpl): AuthService

    @Binds
    @Singleton
    fun bindAuthObservable(impl: AccountRepositoryImpl): AuthObservable
}
