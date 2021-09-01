package me.rutrackersearch.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerApiFactoryImpl @Inject constructor(
    addAuthHeaderInterceptor: AddAuthHeaderInterceptor,
    refreshTokenInterceptor: RefreshTokenInterceptor,
) : ServerApiFactory {

    private val api: ServerApi by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor(addAuthHeaderInterceptor)
            .addNetworkInterceptor(refreshTokenInterceptor)
            .build()
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
            .create(ServerApi::class.java)
    }

    override fun get(): ServerApi = api
}
