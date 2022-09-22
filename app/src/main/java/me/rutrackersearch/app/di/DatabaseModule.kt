package me.rutrackersearch.app.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.data.database.AppDatabase
import me.rutrackersearch.data.database.DatabaseFactory
import me.rutrackersearch.data.database.DatabaseFactoryImpl
import me.rutrackersearch.data.database.dao.BookmarkDao
import me.rutrackersearch.data.database.dao.FavoriteTopicDao
import me.rutrackersearch.data.database.dao.HistoryTopicDao
import me.rutrackersearch.data.database.dao.SearchHistoryDao
import me.rutrackersearch.data.database.dao.SuggestDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {
    @Binds
    @Singleton
    fun databaseFactory(impl: DatabaseFactoryImpl): DatabaseFactory

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(factory: DatabaseFactory): AppDatabase = factory.get()

        @Provides
        @Singleton
        fun provideSuggestDao(db: AppDatabase): SuggestDao = db.suggestDao()

        @Provides
        @Singleton
        fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao = db.searchHistoryDao()

        @Provides
        @Singleton
        fun provideHistoryTopicDao(db: AppDatabase): HistoryTopicDao = db.historyTopicDao()

        @Provides
        @Singleton
        fun provideFavoriteTopicDao(db: AppDatabase): FavoriteTopicDao = db.favoriteTopicDao()

        @Provides
        @Singleton
        fun provideBookmarkDao(db: AppDatabase): BookmarkDao = db.bookmarkDao()
    }
}
