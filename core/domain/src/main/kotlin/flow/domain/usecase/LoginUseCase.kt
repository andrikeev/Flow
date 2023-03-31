package flow.domain.usecase

import flow.auth.api.AuthService
import flow.dispatchers.api.Dispatchers
import flow.models.auth.AuthResult
import flow.work.api.BackgroundService
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authService: AuthService,
    private val backgroundService: BackgroundService,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): AuthResult {
        return withContext(dispatchers.default) {
            runCatching { authService.login(username, password, captchaSid, captchaCode, captchaValue) }
                .fold(
                    onSuccess = { result ->
                        if (result == AuthResult.Success) {
                            backgroundService.loadFavorites()
                        }
                        result
                    },
                    onFailure = AuthResult::Error
                )
        }
    }
}
