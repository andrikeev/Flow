package flow.menu

import androidx.annotation.StringRes
import flow.models.settings.Endpoint
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme

internal sealed interface MenuAction {
    data object AboutClick : MenuAction
    data object ClearBookmarksConfirmation : MenuAction
    data object ClearFavoritesConfirmation : MenuAction
    data object ClearHistoryConfirmation : MenuAction
    data class ConfirmableAction(
        @StringRes val title: Int,
        @StringRes val confirmationMessage: Int,
        val onConfirmAction: () -> Unit,
    ) : MenuAction
    data object LoginClick : MenuAction
    data object PrivacyPolicyClick : MenuAction
    data object RightsClick : MenuAction
    data object SendFeedbackClick : MenuAction
    data class SetBookmarksSyncPeriod(val syncPeriod: SyncPeriod) : MenuAction
    data class SetEndpoint(val endpoint: Endpoint) : MenuAction
    data class SetFavoritesSyncPeriod(val syncPeriod: SyncPeriod) : MenuAction
    data class SetTheme(val theme: Theme) : MenuAction
}
