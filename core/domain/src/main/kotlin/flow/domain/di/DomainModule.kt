package flow.domain.di

import flow.domain.usecase.AddCommentUseCase
import flow.domain.usecase.AddLocalFavoriteUseCase
import flow.domain.usecase.AddRemoteFavoriteUseCase
import flow.domain.usecase.AddSearchHistoryUseCase
import flow.domain.usecase.AddSuggestUseCase
import flow.domain.usecase.AppLaunchedUseCase
import flow.domain.usecase.AppLaunchedUseCaseImpl
import flow.domain.usecase.ClearBookmarksUseCase
import flow.domain.usecase.ClearHistoryUseCase
import flow.domain.usecase.ClearLocalFavoritesUseCase
import flow.domain.usecase.DisableRatingRequestUseCase
import flow.domain.usecase.DisableRatingRequestUseCaseImpl
import flow.domain.usecase.DownloadTorrentUseCase
import flow.domain.usecase.EnrichFilterUseCase
import flow.domain.usecase.EnrichTopicUseCase
import flow.domain.usecase.EnrichTopicsUseCase
import flow.domain.usecase.EnsureForumLoadUseCase
import flow.domain.usecase.GetCategoryUseCase
import flow.domain.usecase.GetForumUseCase
import flow.domain.usecase.GetRatingStoreUseCase
import flow.domain.usecase.GetRatingStoreUseCaseImpl
import flow.domain.usecase.GetTopicUseCase
import flow.domain.usecase.IsAuthorizedUseCase
import flow.domain.usecase.LoadFavoritesUseCase
import flow.domain.usecase.LoginUseCase
import flow.domain.usecase.LogoutUseCase
import flow.domain.usecase.LogoutUseCaseImpl
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ObserveAuthStateUseCaseImpl
import flow.domain.usecase.ObserveBookmarksUseCase
import flow.domain.usecase.ObserveCategoryModelUseCase
import flow.domain.usecase.ObserveCategoryPagingDataUseCase
import flow.domain.usecase.ObserveEndpointStatusUseCase
import flow.domain.usecase.ObserveEndpointStatusUseCaseImpl
import flow.domain.usecase.ObserveFavoriteStateUseCase
import flow.domain.usecase.ObserveFavoritesUseCase
import flow.domain.usecase.ObserveRatingRequestUseCase
import flow.domain.usecase.ObserveRatingRequestUseCaseImpl
import flow.domain.usecase.ObserveSearchHistoryUseCase
import flow.domain.usecase.ObserveSearchPagingDataUseCase
import flow.domain.usecase.ObserveSettingsUseCase
import flow.domain.usecase.ObserveSuggestsUseCase
import flow.domain.usecase.ObserveTopicPagingDataUseCase
import flow.domain.usecase.ObserveVisitedUseCase
import flow.domain.usecase.PinSearchHistoryUseCase
import flow.domain.usecase.PostponeRatingRequestUseCase
import flow.domain.usecase.PostponeRatingRequestUseCaseImpl
import flow.domain.usecase.RefreshFavoritesUseCase
import flow.domain.usecase.RefreshForumUseCase
import flow.domain.usecase.RemoveLocalFavoriteUseCase
import flow.domain.usecase.RemoveRemoteFavoriteUseCase
import flow.domain.usecase.RemoveSearchHistoryUseCase
import flow.domain.usecase.SetBookmarksSyncPeriodUseCase
import flow.domain.usecase.SetFavoritesSyncPeriodUseCase
import flow.domain.usecase.SetProxyUseCase
import flow.domain.usecase.SetThemeUseCase
import flow.domain.usecase.SyncBookmarksUseCase
import flow.domain.usecase.SyncFavoritesUseCase
import flow.domain.usecase.ToggleBookmarkUseCase
import flow.domain.usecase.ToggleFavoriteUseCase
import flow.domain.usecase.UnpinSearchHistoryUseCase
import flow.domain.usecase.UpdateBookmarkUseCase
import flow.domain.usecase.ValidateInputUseCase
import flow.domain.usecase.VisitCategoryUseCase
import flow.domain.usecase.VisitTopicUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for the domain use cases. Interface-backed use cases keep singleton
 * semantics (matching the previous Hilt @Singleton @Binds); the rest are factories
 * (unscoped, a new instance per request, as with unscoped Hilt @Inject constructors).
 */
val domainModule = module {
    singleOf(::AppLaunchedUseCaseImpl) bind AppLaunchedUseCase::class
    singleOf(::DisableRatingRequestUseCaseImpl) bind DisableRatingRequestUseCase::class
    singleOf(::GetRatingStoreUseCaseImpl) bind GetRatingStoreUseCase::class
    singleOf(::LogoutUseCaseImpl) bind LogoutUseCase::class
    singleOf(::ObserveAuthStateUseCaseImpl) bind ObserveAuthStateUseCase::class
    singleOf(::ObserveEndpointStatusUseCaseImpl) bind ObserveEndpointStatusUseCase::class
    singleOf(::ObserveRatingRequestUseCaseImpl) bind ObserveRatingRequestUseCase::class
    singleOf(::PostponeRatingRequestUseCaseImpl) bind PostponeRatingRequestUseCase::class

    factoryOf(::AddCommentUseCase)
    factoryOf(::AddLocalFavoriteUseCase)
    factoryOf(::AddRemoteFavoriteUseCase)
    factoryOf(::AddSearchHistoryUseCase)
    factoryOf(::AddSuggestUseCase)
    factoryOf(::ClearBookmarksUseCase)
    factoryOf(::ClearHistoryUseCase)
    factoryOf(::ClearLocalFavoritesUseCase)
    factoryOf(::DownloadTorrentUseCase)
    factoryOf(::EnrichFilterUseCase)
    factoryOf(::EnrichTopicUseCase)
    factoryOf(::EnrichTopicsUseCase)
    factoryOf(::EnsureForumLoadUseCase)
    factoryOf(::GetCategoryUseCase)
    factoryOf(::GetForumUseCase)
    factoryOf(::GetTopicUseCase)
    factoryOf(::IsAuthorizedUseCase)
    factoryOf(::LoadFavoritesUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::ObserveBookmarksUseCase)
    factoryOf(::ObserveCategoryModelUseCase)
    factoryOf(::ObserveCategoryPagingDataUseCase)
    factoryOf(::ObserveFavoriteStateUseCase)
    factoryOf(::ObserveFavoritesUseCase)
    factoryOf(::ObserveSearchHistoryUseCase)
    factoryOf(::ObserveSearchPagingDataUseCase)
    factoryOf(::ObserveSettingsUseCase)
    factoryOf(::ObserveSuggestsUseCase)
    factoryOf(::ObserveTopicPagingDataUseCase)
    factoryOf(::ObserveVisitedUseCase)
    factoryOf(::PinSearchHistoryUseCase)
    factoryOf(::RefreshFavoritesUseCase)
    factoryOf(::RefreshForumUseCase)
    factoryOf(::RemoveLocalFavoriteUseCase)
    factoryOf(::RemoveRemoteFavoriteUseCase)
    factoryOf(::RemoveSearchHistoryUseCase)
    factoryOf(::SetBookmarksSyncPeriodUseCase)
    factoryOf(::SetFavoritesSyncPeriodUseCase)
    factoryOf(::SetProxyUseCase)
    factoryOf(::SetThemeUseCase)
    factoryOf(::SyncBookmarksUseCase)
    factoryOf(::SyncFavoritesUseCase)
    factoryOf(::ToggleBookmarkUseCase)
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::UnpinSearchHistoryUseCase)
    factoryOf(::UpdateBookmarkUseCase)
    factoryOf(::ValidateInputUseCase)
    factoryOf(::VisitCategoryUseCase)
    factoryOf(::VisitTopicUseCase)
}
