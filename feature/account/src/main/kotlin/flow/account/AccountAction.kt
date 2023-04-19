package flow.account

internal sealed interface AccountAction {
    object LoginClick : AccountAction
    object LogoutClick : AccountAction
    object ConfirmLogoutClick : AccountAction
}
