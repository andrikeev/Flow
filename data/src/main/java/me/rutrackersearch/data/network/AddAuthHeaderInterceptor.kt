package me.rutrackersearch.data.network

import me.rutrackersearch.data.auth.AuthObservable
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAuthHeaderInterceptor @Inject constructor(
    private val authObservable: AuthObservable,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().apply {
                authObservable.token?.also { token ->
                    addHeader("Auth-Token", token)
                }
            }.build()
        )
    }
}
