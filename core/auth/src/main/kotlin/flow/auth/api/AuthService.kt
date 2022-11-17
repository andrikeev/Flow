package flow.auth.api

import flow.auth.models.AuthResponse

interface AuthService {
    suspend fun login(
        username: String,
        password: String,
        captchaSid: String? = null,
        captchaCode: String? = null,
        captchaValue: String? = null,
    ): AuthResponse
}
