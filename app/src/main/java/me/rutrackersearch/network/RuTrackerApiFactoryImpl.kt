package me.rutrackersearch.network

import me.rutrackersearch.auth.AuthObservable
import me.rutrackersearch.network.rutracker.RuTrackerApi
import me.rutrackersearch.network.rutracker.RuTrackerApiImpl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import javax.inject.Inject

class RuTrackerApiFactoryImpl @Inject constructor(
    private val authObservable: AuthObservable,
    private val hostProvider: HostProvider,
    private val interceptors: Set<@JvmSuppressWildcards Interceptor>,
): RuTrackerApiFactory {

    override fun create(): RuTrackerApi {
        val okHttpClient = OkHttpClient.Builder().apply {
            interceptors.forEach(this::addNetworkInterceptor)
        }.build()
        return RuTrackerApiImpl(okHttpClient, hostProvider)
    }
}
