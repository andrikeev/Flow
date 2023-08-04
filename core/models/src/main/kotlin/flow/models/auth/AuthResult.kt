package flow.models.auth

sealed interface AuthResult {
    data object Success : AuthResult
    data class WrongCredits(val captcha: Captcha?) : AuthResult
    data class CaptchaRequired(val captcha: Captcha) : AuthResult
    data class Error(val error: Throwable) : AuthResult
}

data class Captcha(
    val id: String,
    val code: String,
    val url: String,
)
