package flow.network.interceptors

import flow.auth.api.AuthRepository
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AddAuthHeaderInterceptor @Inject constructor(
    private val authRepository: AuthRepository,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().apply {
                authRepository.getToken()?.let {
                    header("Cookie", it)
                }
            }.build()
        )
    }
}
