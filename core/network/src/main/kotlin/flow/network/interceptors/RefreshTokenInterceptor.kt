package flow.network.interceptors

import flow.auth.api.AuthRepository
import flow.auth.api.AuthService
import flow.auth.models.AuthResponse
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

internal class RefreshTokenInterceptor @Inject constructor(
    private val authRepository: AuthRepository,
    private val authService: AuthService,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        var response = chain.proceed(request)

        if (!request.isRetry() && response.code == 401) {
            runBlocking {
                if (refreshToken()) {
                    response.close()
                    response = chain.proceed(request.markAsRetry())
                }
            }
        }
        return response
    }

    private suspend fun refreshToken(): Boolean {
        val account = authRepository.getAccount()
        return if (account != null) {
            val response: AuthResponse = authService.login(
                username = account.name,
                password = account.password,
            )
            if (response is AuthResponse.Success) {
                authRepository.saveAccount(response.account)
                true
            } else {
                authRepository.clear()
                false
            }
        } else {
            false
        }
    }

    private companion object {
        object Retry

        fun Request.isRetry() = tag(Retry::class.java) != null
        fun Request.markAsRetry() = newBuilder().tag(Retry::class.java, Retry).build()
    }
}
