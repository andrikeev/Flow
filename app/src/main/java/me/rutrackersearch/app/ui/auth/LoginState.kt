package me.rutrackersearch.app.ui.auth

import me.rutrackersearch.auth.models.Captcha
import me.rutrackersearch.models.InputState

data class LoginState(
    val isLoading: Boolean = false,
    val usernameInput: InputState = InputState.Initial,
    val passwordInput: InputState = InputState.Initial,
    val captchaInput: InputState = InputState.Initial,
    val captcha: Captcha? = null,
) {
    val hasCaptcha: Boolean = captcha != null

    val isValid: Boolean = usernameInput.isValid() &&
            passwordInput.isValid() &&
            (!hasCaptcha || captchaInput.isValid())
}
