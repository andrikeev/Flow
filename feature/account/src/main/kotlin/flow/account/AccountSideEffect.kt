package flow.account

internal sealed interface AccountSideEffect {
    object HideLogoutConfirmation : AccountSideEffect
    object OpenLogin : AccountSideEffect
    object ShowLogoutConfirmation : AccountSideEffect
}
