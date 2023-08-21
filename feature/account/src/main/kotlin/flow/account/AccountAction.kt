package flow.account

internal sealed interface AccountAction {
    data object LoginClick : AccountAction
    data object LogoutClick : AccountAction
    data object ConfirmLogoutClick : AccountAction
}
