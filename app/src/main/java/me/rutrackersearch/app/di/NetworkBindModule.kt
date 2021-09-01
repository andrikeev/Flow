package me.rutrackersearch.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.rutrackersearch.data.download.TorrentDownloadServiceImpl
import me.rutrackersearch.data.network.ServerApiFactory
import me.rutrackersearch.data.network.ServerApiFactoryImpl
import me.rutrackersearch.domain.service.TorrentDownloadService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkBindModule {
    @Binds
    @Singleton
    fun serverApiFactory(impl: ServerApiFactoryImpl): ServerApiFactory

    @Binds
    @Singleton
    fun downloadService(impl: TorrentDownloadServiceImpl): TorrentDownloadService
}
