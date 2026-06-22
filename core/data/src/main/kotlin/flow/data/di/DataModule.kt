package flow.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.data.api.service.ConnectionService
import flow.data.api.service.FavoritesService
import flow.data.api.service.ForumService
import flow.data.api.service.SearchService
import flow.data.api.service.StoreService
import flow.data.api.service.TopicService
import flow.data.api.service.TorrentService
import flow.data.impl.service.ConnectionServiceImpl
import flow.data.impl.service.FavoritesServiceImpl
import flow.data.impl.service.ForumServiceImpl
import flow.data.impl.service.SearchServiceImpl
import flow.data.impl.service.StoreServiceImpl
import flow.data.impl.service.TopicServiceImpl
import flow.data.impl.service.TorrentServiceImpl
import javax.inject.Singleton

/**
 * Hilt module for the data services. The repositories have moved to Koin
 * (repositoryModule); services consume them through the inverse-bridge in :app and
 * will migrate to Koin together with network:impl (their NetworkApi dependency).
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
    fun networkConnectionService(impl: ConnectionServiceImpl): ConnectionService

    @Binds
    @Singleton
    fun searchService(impl: SearchServiceImpl): SearchService

    @Binds
    @Singleton
    fun storeService(impl: StoreServiceImpl): StoreService

    @Binds
    @Singleton
    fun topicService(impl: TopicServiceImpl): TopicService

    @Binds
    @Singleton
    fun torrentService(impl: TorrentServiceImpl): TorrentService
}
