package me.rutrackersearch.domain.entity.user

sealed interface AuthState {

    data class Authorized(val account: Account) : AuthState

    object Unauthorized : AuthState
}

fun AuthState?.isAuthorized() = this is AuthState.Authorized