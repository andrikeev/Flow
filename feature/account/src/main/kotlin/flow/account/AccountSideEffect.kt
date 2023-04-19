package flow.account

internal sealed interface AccountSideEffect {
    object OpenLogin : AccountSideEffect
    object ShowLogoutConfirmation : AccountSideEffect
}
