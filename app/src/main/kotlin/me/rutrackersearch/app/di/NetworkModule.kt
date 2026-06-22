package me.rutrackersearch.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.network.api.ImageLoader
import flow.network.api.NetworkApi
import flow.network.api.ProxyController
import flow.network.data.NetworkApiRepository
import org.koin.core.context.GlobalContext
import javax.inject.Singleton

/**
 * Inverse-bridge: Koin owns the network layer (networkModule); remaining Hilt consumers
 * (data services -> NetworkApi; domain -> NetworkApiRepository; FlowApplication ->
 * ImageLoader/ProxyController) read them from Koin during the migration.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton fun networkApi(): NetworkApi = GlobalContext.get().get()

    @Provides @Singleton fun imageLoader(): ImageLoader = GlobalContext.get().get()

    @Provides @Singleton fun proxyController(): ProxyController = GlobalContext.get().get()

    @Provides @Singleton fun networkApiRepository(): NetworkApiRepository = GlobalContext.get().get()
}
