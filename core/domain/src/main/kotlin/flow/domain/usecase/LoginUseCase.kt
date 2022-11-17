package flow.domain.usecase

import flow.auth.api.AuthService
import flow.auth.models.AuthResponse
import javax.inject.Inject

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
