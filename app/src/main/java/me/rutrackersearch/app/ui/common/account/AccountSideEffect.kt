package me.rutrackersearch.app.ui.common.account

sealed interface AccountSideEffect {
    object OpenLogin : AccountSideEffect
}
