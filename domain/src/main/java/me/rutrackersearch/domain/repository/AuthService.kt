package me.rutrackersearch.domain.repository

import me.rutrackersearch.domain.entity.auth.AuthResponse

interface AuthService {
    suspend fun login(
        username: String,
        password: String,
        captchaSid: String? = null,
        captchaCode: String? = null,
        captchaValue: String? = null,
    ): AuthResponse
}
