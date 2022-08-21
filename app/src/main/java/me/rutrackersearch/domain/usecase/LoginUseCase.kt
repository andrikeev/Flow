package me.rutrackersearch.domain.usecase

import me.rutrackersearch.auth.AuthService
import me.rutrackersearch.auth.models.AuthResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginUseCase @Inject constructor(
    private val authService: AuthService
) {
    suspend operator fun invoke(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): AuthResponse {
        return authService.login(username, password, captchaSid, captchaCode, captchaValue)
    }
}
