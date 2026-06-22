package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.data.api.repository.BookmarksRepository
import flow.data.api.repository.FavoriteSearchRepository
import flow.data.api.repository.FavoritesRepository
import flow.data.api.repository.ForumRepository
import flow.data.api.repository.RatingRepository
import flow.data.api.repository.SearchHistoryRepository
import flow.data.api.repository.SettingsRepository
import flow.data.api.repository.SuggestsRepository
import flow.data.api.repository.VisitedRepository
import org.koin.core.context.GlobalContext
import javax.inject.Singleton

/**
 * Inverse-bridge: Koin owns the data repositories (repositoryModule); remaining Hilt
 * consumers (data services, domain, network:impl ProxyController) read them from Koin
 * during the migration. Removed once those consumers move to Koin.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides @Singleton fun bookmarksRepository(): BookmarksRepository = GlobalContext.get().get()

    @Provides @Singleton fun favoriteSearchRepository(): FavoriteSearchRepository = GlobalContext.get().get()

    @Provides @Singleton fun favoritesRepository(): FavoritesRepository = GlobalContext.get().get()

    @Provides @Singleton fun forumRepository(): ForumRepository = GlobalContext.get().get()

    @Provides @Singleton fun ratingRepository(): RatingRepository = GlobalContext.get().get()

    @Provides @Singleton fun searchHistoryRepository(): SearchHistoryRepository = GlobalContext.get().get()

    @Provides @Singleton fun settingsRepository(): SettingsRepository = GlobalContext.get().get()

    @Provides @Singleton fun suggestsRepository(): SuggestsRepository = GlobalContext.get().get()

    @Provides @Singleton fun visitedRepository(): VisitedRepository = GlobalContext.get().get()
}
