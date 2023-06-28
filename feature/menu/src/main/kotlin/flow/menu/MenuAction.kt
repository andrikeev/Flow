package flow.menu

import androidx.annotation.StringRes
import flow.models.settings.Endpoint
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme

internal sealed interface MenuAction {
    object AboutClick : MenuAction
    object ClearBookmarksConfirmation : MenuAction
    object ClearFavoritesConfirmation : MenuAction
    object ClearHistoryConfirmation : MenuAction
    data class ConfirmableAction(
        @StringRes val title: Int,
        @StringRes val confirmationMessage: Int,
        val onConfirmAction: () -> Unit,
    ) : MenuAction
    object LoginClick : MenuAction
    object MyTipsClick : MenuAction
    object NetMonetClick : MenuAction
    object PayPalClick : MenuAction
    object PrivacyPolicyClick : MenuAction
    object RightsClick : MenuAction
    object SendFeedbackClick : MenuAction
    data class SetBookmarksSyncPeriod(val syncPeriod: SyncPeriod) : MenuAction
    data class SetEndpoint(val endpoint: Endpoint) : MenuAction
    data class SetFavoritesSyncPeriod(val syncPeriod: SyncPeriod) : MenuAction
    data class SetTheme(val theme: Theme) : MenuAction
}
