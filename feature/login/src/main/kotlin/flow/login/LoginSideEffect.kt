package flow.login

sealed interface LoginSideEffect {
    data class Error(val error: Throwable) : LoginSideEffect
    data object HideKeyboard : LoginSideEffect
    data object Success : LoginSideEffect
}
