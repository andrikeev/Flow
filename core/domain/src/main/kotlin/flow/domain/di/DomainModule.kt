package flow.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.domain.usecase.DisableRatingRequestUseCase
import flow.domain.usecase.DisableRatingRequestUseCaseImpl
import flow.domain.usecase.GetRatingStoreUseCase
import flow.domain.usecase.GetRatingStoreUseCaseImpl
import flow.domain.usecase.AppLaunchedUseCase
import flow.domain.usecase.AppLaunchedUseCaseImpl
import flow.domain.usecase.LogoutUseCase
import flow.domain.usecase.LogoutUseCaseImpl
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ObserveAuthStateUseCaseImpl
import flow.domain.usecase.ObserveRatingRequestUseCase
import flow.domain.usecase.ObserveRatingRequestUseCaseImpl
import flow.domain.usecase.PostponeRatingRequestUseCase
import flow.domain.usecase.PostponeRatingRequestUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DomainModule {
    @Binds
    @Singleton
    fun disableRatingRequestUseCase(impl: DisableRatingRequestUseCaseImpl): DisableRatingRequestUseCase

    @Binds
    @Singleton
    fun getRatingStoreUseCase(impl: GetRatingStoreUseCaseImpl): GetRatingStoreUseCase

    @Binds
    @Singleton
    fun incrementLaunchCountUseCase(impl: AppLaunchedUseCaseImpl): AppLaunchedUseCase

    @Binds
    @Singleton
    fun logoutUseCase(impl: LogoutUseCaseImpl): LogoutUseCase

    @Binds
    @Singleton
    fun observeAuthStateUseCase(impl: ObserveAuthStateUseCaseImpl): ObserveAuthStateUseCase

    @Binds
    @Singleton
    fun observeRatingRequestUseCase(impl: ObserveRatingRequestUseCaseImpl): ObserveRatingRequestUseCase

    @Binds
    @Singleton
    fun postponeRatingRequestUseCase(impl: PostponeRatingRequestUseCaseImpl): PostponeRatingRequestUseCase
}
