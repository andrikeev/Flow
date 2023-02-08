package flow.domain.usecase

import flow.auth.api.AuthService
import flow.models.auth.AuthResult
import flow.work.api.BackgroundService
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authService: AuthService,
    private val backgroundService: BackgroundService,
) {
    suspend operator fun invoke(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): AuthResult = authService.login(username, password, captchaSid, captchaCode, captchaValue)
        .also { authResult ->
            if (authResult == AuthResult.Success) {
                backgroundService.loadFavorites()
            }
        }
}
