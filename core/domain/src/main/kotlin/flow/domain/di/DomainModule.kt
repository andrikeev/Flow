package flow.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.domain.usecase.LogoutUseCase
import flow.domain.usecase.LogoutUseCaseImpl
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ObserveAuthStateUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DomainModule {
    @Binds
    @Singleton
    fun logoutUseCase(impl: LogoutUseCaseImpl): LogoutUseCase

    @Binds
    @Singleton
    fun observeAuthStateUseCase(impl: ObserveAuthStateUseCaseImpl): ObserveAuthStateUseCase
}
