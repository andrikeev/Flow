package me.rutrackersearch.app.ui.auth

sealed interface LoginSideEffect {
    data class Error(val error: Throwable) : LoginSideEffect
    object HideKeyboard : LoginSideEffect
    object Success : LoginSideEffect
}
