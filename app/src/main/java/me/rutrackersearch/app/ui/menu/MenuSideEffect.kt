package me.rutrackersearch.app.ui.menu

sealed interface MenuSideEffect {
    object OpenLogin : MenuSideEffect
    data class OpenLink(val link: String) : MenuSideEffect
}
