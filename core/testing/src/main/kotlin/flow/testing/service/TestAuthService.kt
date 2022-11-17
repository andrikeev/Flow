package flow.testing.service

import flow.auth.api.AuthService
import flow.auth.models.AuthResponse

class TestAuthService : AuthService {
    var response: AuthResponse = AuthResponse.Error(RuntimeException())

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?
    ): AuthResponse = response
}
