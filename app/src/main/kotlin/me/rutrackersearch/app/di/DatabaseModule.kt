package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.database.dao.BookmarkDao
import flow.database.dao.FavoriteSearchDao
import flow.database.dao.FavoriteTopicDao
import flow.database.dao.ForumCategoryDao
import flow.database.dao.ForumMetadataDao
import flow.database.dao.SearchHistoryDao
import flow.database.dao.SuggestDao
import flow.database.dao.VisitedTopicDao
import org.koin.core.context.GlobalContext
import javax.inject.Singleton

/**
 * Inverse-bridge: Koin owns the Room database and DAOs (databaseModule); remaining Hilt
 * consumers (core:data repositories) read the DAOs from Koin during the migration.
 * Removed in the data phase once core:data is migrated to Koin.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton fun bookmarkDao(): BookmarkDao = GlobalContext.get().get()

    @Provides @Singleton fun favoriteSearchDao(): FavoriteSearchDao = GlobalContext.get().get()

    @Provides @Singleton fun favoriteTopicDao(): FavoriteTopicDao = GlobalContext.get().get()

    @Provides @Singleton fun forumCategoryDao(): ForumCategoryDao = GlobalContext.get().get()

    @Provides @Singleton fun forumMetadataDao(): ForumMetadataDao = GlobalContext.get().get()

    @Provides @Singleton fun searchHistoryDao(): SearchHistoryDao = GlobalContext.get().get()

    @Provides @Singleton fun suggestDao(): SuggestDao = GlobalContext.get().get()

    @Provides @Singleton fun visitedTopicDao(): VisitedTopicDao = GlobalContext.get().get()
}
