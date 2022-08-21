package me.rutrackersearch.data.network

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import me.rutrackersearch.auth.AuthService
import me.rutrackersearch.auth.models.AuthResponse
import me.rutrackersearch.domain.repository.AccountRepository
import me.rutrackersearch.models.user.Account
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class RefreshTokenInterceptor @Inject constructor(
    private val accountRepository: AccountRepository,
    private val authService: AuthService,
) : Interceptor {
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
        val account = accountRepository.observeAccount().firstOrNull()
        return if (account != null) {
            val response: AuthResponse = authService.login(
                username = account.name,
                password = account.password,
            )
            if (response is AuthResponse.Success) {
                accountRepository.saveAccount(
                    Account(
                        response.accountData.id,
                        account.name,
                        account.password,
                        response.accountData.token,
                        response.accountData.avatarUrl,
                    )
                )
                true
            } else {
                accountRepository.clear()
                false
            }
        } else {
            false
        }
    }

    private companion object {
        object Retried

        fun Request.isRetry() = tag(Retried::class.java) != null
        fun Request.markAsRetry() = newBuilder().tag(Retried::class.java, Retried).build()
    }
}
