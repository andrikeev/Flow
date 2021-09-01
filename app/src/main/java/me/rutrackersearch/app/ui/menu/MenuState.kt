package me.rutrackersearch.app.ui.menu

import me.rutrackersearch.domain.entity.settings.SyncPeriod
import me.rutrackersearch.domain.entity.settings.Theme

data class MenuState(
    val theme: Theme = Theme.SYSTEM,
    val favoritesSyncPeriod: SyncPeriod = SyncPeriod.OFF,
    val bookmarksSyncPeriod: SyncPeriod = SyncPeriod.OFF,
)
