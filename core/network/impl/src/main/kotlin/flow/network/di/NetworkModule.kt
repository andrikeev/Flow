package flow.network.di

import coil3.SingletonImageLoader
import flow.network.api.ImageLoader
import flow.network.api.NetworkApi
import flow.network.api.ProxyController
import flow.network.data.ImageLoaderFactoryImpl
import flow.network.data.NetworkApiRepository
import flow.network.data.NetworkApiRepositoryImpl
import flow.network.data.NetworkLogger
import flow.network.impl.ImageLoaderImpl
import flow.network.impl.ProxyControllerImpl
import flow.network.impl.SwitchingNetworkApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for the network layer. SettingsRepository/Dispatchers/LoggerFactory come
 * from the Koin graph. The OkHttpClient <-> ProxyController cycle is broken by passing a
 * lazy `() -> OkHttpClient` into ProxyControllerImpl. Interceptors are collected via
 * getAll() (Chucker is added by the debug-only module, see networkDebugModules()).
 *
 * On Android the public types are exposed to remaining Hilt consumers via an
 * inverse-bridge in :app until those consumers move to Koin.
 */
val networkModule = module {
    singleOf(::NetworkLogger)
    singleOf(::NetworkApiRepositoryImpl) bind NetworkApiRepository::class
    singleOf(::SwitchingNetworkApi) bind NetworkApi::class
    singleOf(::ImageLoaderFactoryImpl) bind SingletonImageLoader.Factory::class
    singleOf(::ImageLoaderImpl) bind ImageLoader::class

    single { ProxyControllerImpl(get(), get(), { get<OkHttpClient>() }, get()) } bind ProxyController::class

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .proxySelector(get<ProxyControllerImpl>())
            .proxyAuthenticator { _, response -> get<ProxyControllerImpl>().authenticate(response) }
            .apply { getAll<Interceptor>().forEach(::addInterceptor) }
            .build()
    }
}
