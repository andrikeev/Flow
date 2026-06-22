package flow.data.di

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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for the data services. Context-only services use the Koin graph
 * (androidContext); network-backed services depend on NetworkApi/AuthService/TokenProvider
 * (also in Koin). Validated by ServiceModuleTest.
 */
val serviceModule = module {
    singleOf(::ConnectionServiceImpl) bind ConnectionService::class
    singleOf(::StoreServiceImpl) bind StoreService::class
    singleOf(::FavoritesServiceImpl) bind FavoritesService::class
    singleOf(::ForumServiceImpl) bind ForumService::class
    singleOf(::SearchServiceImpl) bind SearchService::class
    singleOf(::TopicServiceImpl) bind TopicService::class
    singleOf(::TorrentServiceImpl) bind TorrentService::class
}
