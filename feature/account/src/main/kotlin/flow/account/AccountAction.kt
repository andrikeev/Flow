package flow.account

internal sealed interface AccountAction {
    object ConfirmLogoutClick : AccountAction
    object CancelLogoutClick : AccountAction
    object LoginClick : AccountAction
    object LogoutClick : AccountAction
}
