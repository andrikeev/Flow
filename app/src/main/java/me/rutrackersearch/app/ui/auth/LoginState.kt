package me.rutrackersearch.app.ui.auth

import me.rutrackersearch.domain.entity.auth.Captcha

sealed interface LoginStatus {
    object Initial : LoginStatus
    object Loading : LoginStatus
    object Success : LoginStatus
    data class Error(val error: Throwable? = null) : LoginStatus
}

data class LoginState(
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val usernameInput: InputState = InputState.Initial,
    val passwordInput: InputState = InputState.Initial,
    val captcha: Captcha? = null,
    val captchaInput: InputState = InputState.Initial,
    val error: Throwable? = null,
) {
    val hasCaptcha: Boolean = captcha != null

    val isValid: Boolean = usernameInput.isValid() &&
        passwordInput.isValid() &&
        (!hasCaptcha || captchaInput.isValid())
}
