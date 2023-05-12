package flow.login

import androidx.compose.ui.text.input.TextFieldValue
import flow.models.auth.Captcha

data class LoginState(
    val isLoading: Boolean = false,
    val usernameInput: InputState = InputState.Initial,
    val passwordInput: InputState = InputState.Initial,
    val captchaInput: InputState = InputState.Initial,
    val captcha: Captcha? = null,
)

sealed interface InputState {
    val value: TextFieldValue
        get() = TextFieldValue()

    object Initial : InputState

    object Empty : InputState

    data class Valid(override val value: TextFieldValue) : InputState

    data class Invalid(override val value: TextFieldValue) : InputState

    fun isValid() = this is Valid

    fun isError() = this is Invalid || this is Empty
}

val LoginState.hasCaptcha: Boolean
    get() = captcha != null

val LoginState.isValid: Boolean
    get() = usernameInput.isValid() &&
            passwordInput.isValid() &&
            (!hasCaptcha || captchaInput.isValid())
