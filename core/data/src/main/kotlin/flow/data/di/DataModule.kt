package flow.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.data.api.service.FavoritesService
import flow.data.api.service.ForumService
import flow.data.api.service.SearchService
import flow.data.api.service.TopicService
import flow.data.api.service.TorrentService
import flow.data.impl.service.FavoritesServiceImpl
import flow.data.impl.service.ForumServiceImpl
import flow.data.impl.service.SearchServiceImpl
import flow.data.impl.service.TopicServiceImpl
import flow.data.impl.service.TorrentServiceImpl
import javax.inject.Singleton

/**
 * Hilt module for the network-backed data services (still on Hilt because they depend on
 * NetworkApi/AuthService). Repositories and the Context-only services (Connection, Store)
 * have moved to Koin; these migrate together with network:impl + auth.
 */
@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    @Singleton
    fun favoritesService(impl: FavoritesServiceImpl): FavoritesService

    @Binds
    @Singleton
    fun forumService(impl: ForumServiceImpl): ForumService

    @Binds
    @Singleton
    fun searchService(impl: SearchServiceImpl): SearchService

    @Binds
    @Singleton
    fun topicService(impl: TopicServiceImpl): TopicService

    @Binds
    @Singleton
    fun torrentService(impl: TorrentServiceImpl): TorrentService
}
