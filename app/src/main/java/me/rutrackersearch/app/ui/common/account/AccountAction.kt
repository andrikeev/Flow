package me.rutrackersearch.app.ui.common.account

sealed interface AccountAction {
    object LoginClick : AccountAction
    object LogoutClick : AccountAction
}
