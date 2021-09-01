package me.rutrackersearch.app.ui.auth

sealed interface LoginAction {
    data class CaptchaChanged(val value: String) : LoginAction
    data class PasswordChanged(val value: String) : LoginAction
    data class UsernameChanged(val value: String) : LoginAction
    object SubmitClick : LoginAction
}
