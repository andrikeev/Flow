package me.rutrackersearch.app.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import me.rutrackersearch.data.download.TorrentDownloadServiceImpl
import me.rutrackersearch.data.network.AddAuthHeaderInterceptor
import me.rutrackersearch.data.network.RefreshTokenInterceptor
import me.rutrackersearch.domain.service.TorrentDownloadService
import me.rutrackersearch.network.HostProvider
import me.rutrackersearch.network.NetworkApi
import me.rutrackersearch.network.NetworkApiImpl
import me.rutrackersearch.network.RuTrackerApiFactoryImpl
import me.rutrackersearch.network.rutracker.RuTrackerApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule {
    @Binds
    @Singleton
    fun networkApi(impl: NetworkApiImpl): NetworkApi

    @Binds
    @Singleton
    fun downloadService(impl: TorrentDownloadServiceImpl): TorrentDownloadService

    @Binds
    @Singleton
    @IntoSet
    fun addAuthHeaderInterceptor(impl: AddAuthHeaderInterceptor): Interceptor

    @Binds
    @Singleton
    @IntoSet
    fun refreshTokenInterceptor(impl: RefreshTokenInterceptor): Interceptor

    companion object {
        @Provides
        @Singleton
        fun rutrackerApi(impl: RuTrackerApiFactoryImpl): RuTrackerApi {
            return impl.create()
        }

        @Provides
        @Singleton
        fun hostProvider(): HostProvider {
            return object : HostProvider { override val host = "rutracker.org" }
        }

        @Provides
        @Singleton
        fun okHttpClient(interceptors: Set<Interceptor>): OkHttpClient {
            return OkHttpClient.Builder().apply {
                interceptors.forEach(this::addNetworkInterceptor)
            }.build()
        }

        @Provides
        @Singleton
        @IntoSet
        fun httpLoggingInterceptor(): Interceptor {
            return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
}
