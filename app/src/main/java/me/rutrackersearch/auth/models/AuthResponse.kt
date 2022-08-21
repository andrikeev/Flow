package me.rutrackersearch.auth.models

sealed interface AuthResponse {

    data class Success(val accountData: AccountData) : AuthResponse

    data class WrongCredits(val captcha: Captcha?) : AuthResponse

    data class CaptchaRequired(val captcha: Captcha) : AuthResponse

    data class Error(val error: Throwable) : AuthResponse
}
