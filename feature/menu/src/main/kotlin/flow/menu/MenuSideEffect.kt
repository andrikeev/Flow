package flow.menu

import androidx.annotation.StringRes

internal sealed interface MenuSideEffect {
    object OpenLogin : MenuSideEffect
    data class OpenLink(val link: String) : MenuSideEffect
    object ShowAbout : MenuSideEffect
    data class ShowConfirmation(
        @StringRes val confirmationMessage: Int,
        val action: () -> Unit,
    ) : MenuSideEffect

}
