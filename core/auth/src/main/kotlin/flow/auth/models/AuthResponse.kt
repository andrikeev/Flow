package flow.auth.models

import flow.models.user.Account

sealed interface AuthResponse {

    data class Success(val account: Account) : AuthResponse

    data class WrongCredits(val captcha: Captcha?) : AuthResponse

    data class CaptchaRequired(val captcha: Captcha) : AuthResponse

    data class Error(val error: Throwable) : AuthResponse
}
