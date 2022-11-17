package flow.network.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import flow.network.NetworkApi
import flow.network.NetworkApiImpl
import flow.network.interceptors.AddAuthHeaderInterceptor
import flow.network.interceptors.RefreshTokenInterceptor
import flow.network.rutracker.RuTrackerApi
import flow.network.rutracker.RuTrackerApiImpl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkModule {
    @Binds
    @Singleton
    fun networkApi(impl: NetworkApiImpl): NetworkApi

    @Binds
    @Singleton
    fun ruTrackerApi(impl: RuTrackerApiImpl): RuTrackerApi

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
        fun okHttpClient(interceptors: Set<@JvmSuppressWildcards Interceptor>): OkHttpClient {
            return OkHttpClient.Builder().apply {
                interceptors.forEach(this::addNetworkInterceptor)
            }.build()
        }

        @Provides
        @Singleton
        @IntoSet
        fun httpLoggingInterceptor(): Interceptor {
            return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
        }
    }
}
