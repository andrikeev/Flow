package flow.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.data.api.BookmarksRepository
import flow.data.api.FavoritesRepository
import flow.data.api.ForumRepository
import flow.data.api.SearchHistoryRepository
import flow.data.api.SearchRepository
import flow.data.api.SettingsRepository
import flow.data.api.SuggestsRepository
import flow.data.api.TopicHistoryRepository
import flow.data.api.TopicRepository
import flow.data.api.TorrentRepository
import flow.data.impl.BookmarksRepositoryImpl
import flow.data.impl.FavoritesRepositoryImpl
import flow.data.impl.ForumRepositoryImpl
import flow.data.impl.SearchHistoryRepositoryImpl
import flow.data.impl.SearchRepositoryImpl
import flow.data.impl.SettingsRepositoryImpl
import flow.data.impl.SuggestsRepositoryImpl
import flow.data.impl.TopicHistoryRepositoryImpl
import flow.data.impl.TopicRepositoryImpl
import flow.data.impl.TorrentRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    @Singleton
    fun bookmarksRepository(impl: BookmarksRepositoryImpl): BookmarksRepository

    @Binds
    @Singleton
    fun favoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

    @Binds
    @Singleton
    fun forumRepository(impl: ForumRepositoryImpl): ForumRepository

    @Binds
    @Singleton
    fun searchHistoryRepository(impl: SearchHistoryRepositoryImpl): SearchHistoryRepository

    @Binds
    @Singleton
    fun searchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    fun settingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    fun suggestsRepository(impl: SuggestsRepositoryImpl): SuggestsRepository

    @Binds
    @Singleton
    fun topicHistoryRepository(impl: TopicHistoryRepositoryImpl): TopicHistoryRepository

    @Binds
    @Singleton
    fun topicRepository(impl: TopicRepositoryImpl): TopicRepository

    @Binds
    @Singleton
    fun torrentRepository(impl: TorrentRepositoryImpl): TorrentRepository
}
