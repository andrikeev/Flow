package flow.login

import androidx.compose.ui.text.input.TextFieldValue

internal sealed interface LoginAction {
    data class CaptchaChanged(val value: TextFieldValue) : LoginAction
    data class PasswordChanged(val value: TextFieldValue) : LoginAction
    data class UsernameChanged(val value: TextFieldValue) : LoginAction
    data object ReloadCaptchaClick : LoginAction
    data object SubmitClick : LoginAction
}
