package flow.models.user

sealed interface AuthState {
    data class Authorized(
        val name: String,
        val avatarUrl: String?,
    ) : AuthState

    object Unauthorized : AuthState
}

val AuthState?.isAuthorized
    get() = this is AuthState.Authorized
