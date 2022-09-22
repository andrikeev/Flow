package me.rutrackersearch.data.network

import me.rutrackersearch.auth.AuthObservable
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAuthHeaderInterceptor @Inject constructor(
    private val authObservable: AuthObservable,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().apply {
                authObservable.token?.also { token ->
                    header("Cookie", token)
                }
            }.build()
        )
    }
}
