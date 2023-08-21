package flow.account

internal sealed interface AccountSideEffect {
    data object OpenLogin : AccountSideEffect
    data object ShowLogoutConfirmation : AccountSideEffect
}
