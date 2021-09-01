package me.rutrackersearch.app.ui.menu

import me.rutrackersearch.domain.entity.settings.SyncPeriod
import me.rutrackersearch.domain.entity.settings.Theme

sealed interface MenuAction {
    object ClearBookmarksClick : MenuAction
    object ClearFavoritesClick : MenuAction
    object ClearHistoryClick : MenuAction
    object LoginClick : MenuAction
    data class SetBookmarksSyncPeriod(val syncPeriod: SyncPeriod) : MenuAction
    data class SetFavoritesSyncPeriod(val syncPeriod: SyncPeriod) : MenuAction
    data class SetTheme(val theme: Theme) : MenuAction
}
