package flow.data.di

import flow.data.api.repository.BookmarksRepository
import flow.data.api.repository.FavoriteSearchRepository
import flow.data.api.repository.FavoritesRepository
import flow.data.api.repository.ForumRepository
import flow.data.api.repository.RatingRepository
import flow.data.api.repository.SearchHistoryRepository
import flow.data.api.repository.SettingsRepository
import flow.data.api.repository.SuggestsRepository
import flow.data.api.repository.VisitedRepository
import flow.data.impl.repository.BookmarksRepositoryImpl
import flow.data.impl.repository.FavoriteSearchRepositoryImpl
import flow.data.impl.repository.FavoritesRepositoryImpl
import flow.data.impl.repository.ForumRepositoryImpl
import flow.data.impl.repository.RatingRepositoryImpl
import flow.data.impl.repository.SearchHistoryRepositoryImpl
import flow.data.impl.repository.SettingsRepositoryImpl
import flow.data.impl.repository.SuggestsRepositoryImpl
import flow.data.impl.repository.VisitedRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for the data repositories. Dependencies (DAOs, PreferencesStorage,
 * LoggerFactory) are resolved from the Koin graph. Constructor wiring uses singleOf so
 * the graph can be statically validated by RepositoryModuleTest.verify().
 *
 * On Android the repositories are exposed to remaining Hilt consumers (data services,
 * domain) via an inverse-bridge in :app until those migrate to Koin.
 */
val repositoryModule = module {
    singleOf(::BookmarksRepositoryImpl) bind BookmarksRepository::class
    singleOf(::FavoriteSearchRepositoryImpl) bind FavoriteSearchRepository::class
    singleOf(::FavoritesRepositoryImpl) bind FavoritesRepository::class
    singleOf(::ForumRepositoryImpl) bind ForumRepository::class
    singleOf(::RatingRepositoryImpl) bind RatingRepository::class
    singleOf(::SearchHistoryRepositoryImpl) bind SearchHistoryRepository::class
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
    singleOf(::SuggestsRepositoryImpl) bind SuggestsRepository::class
    singleOf(::VisitedRepositoryImpl) bind VisitedRepository::class
}
