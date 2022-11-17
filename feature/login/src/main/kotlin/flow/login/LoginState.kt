package flow.login

import flow.auth.models.Captcha
import flow.models.InputState

data class LoginState(
    val isLoading: Boolean = false,
    val usernameInput: InputState = InputState.Initial,
    val passwordInput: InputState = InputState.Initial,
    val captchaInput: InputState = InputState.Initial,
    val captcha: Captcha? = null,
)

val LoginState.hasCaptcha: Boolean
    get() = captcha != null

val LoginState.isValid: Boolean
    get() = usernameInput.isValid() &&
            passwordInput.isValid() &&
            (!hasCaptcha || captchaInput.isValid())
