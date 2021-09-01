package me.rutrackersearch.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.data.auth.AuthServiceImpl
import me.rutrackersearch.data.repository.AccountRepositoryImpl
import me.rutrackersearch.data.repository.BookmarksRepositoryImpl
import me.rutrackersearch.data.repository.FavoritesRepositoryImpl
import me.rutrackersearch.data.repository.ForumRepositoryImpl
import me.rutrackersearch.data.repository.SearchHistoryRepositoryImpl
import me.rutrackersearch.data.repository.SearchRepositoryImpl
import me.rutrackersearch.data.repository.SettingsRepositoryImpl
import me.rutrackersearch.data.repository.SuggestsRepositoryImpl
import me.rutrackersearch.data.repository.TopicHistoryRepositoryImpl
import me.rutrackersearch.data.repository.TopicRepositoryImpl
import me.rutrackersearch.data.repository.TorrentRepositoryImpl
import me.rutrackersearch.domain.repository.AccountRepository
import me.rutrackersearch.domain.repository.AuthService
import me.rutrackersearch.domain.repository.BookmarksRepository
import me.rutrackersearch.domain.repository.FavoritesRepository
import me.rutrackersearch.domain.repository.ForumRepository
import me.rutrackersearch.domain.repository.SearchHistoryRepository
import me.rutrackersearch.domain.repository.SearchRepository
import me.rutrackersearch.domain.repository.SettingsRepository
import me.rutrackersearch.domain.repository.SuggestsRepository
import me.rutrackersearch.domain.repository.TopicHistoryRepository
import me.rutrackersearch.domain.repository.TopicRepository
import me.rutrackersearch.domain.repository.TorrentRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindAuthService(impl: AuthServiceImpl): AuthService

    @Binds
    @Singleton
    fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    fun bindSearchHistoryRepository(impl: SearchHistoryRepositoryImpl): SearchHistoryRepository

    @Binds
    @Singleton
    fun bindSuggestsRepository(impl: SuggestsRepositoryImpl): SuggestsRepository

    @Binds
    @Singleton
    fun favoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

    @Binds
    @Singleton
    fun torrentRepository(impl: TorrentRepositoryImpl): TorrentRepository

    @Binds
    @Singleton
    fun topicRepository(impl: TopicRepositoryImpl): TopicRepository

    @Binds
    @Singleton
    fun bookmarksRepository(impl: BookmarksRepositoryImpl): BookmarksRepository

    @Binds
    @Singleton
    fun forumRepository(impl: ForumRepositoryImpl): ForumRepository

    @Binds
    @Singleton
    fun searchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    fun topicHistoryRepository(impl: TopicHistoryRepositoryImpl): TopicHistoryRepository

    @Binds
    @Singleton
    fun settingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
