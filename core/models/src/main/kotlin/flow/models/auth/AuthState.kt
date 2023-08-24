package flow.models.auth

sealed interface AuthState {
    data class Authorized(
        val name: String,
        val avatarUrl: String?,
    ) : AuthState

    data object Unauthorized : AuthState
}

val AuthState?.isAuthorized
    get() = this is AuthState.Authorized
