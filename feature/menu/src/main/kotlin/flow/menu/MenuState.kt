package flow.menu

import flow.models.settings.SyncPeriod
import flow.models.settings.Theme

internal data class MenuState(
    val theme: Theme = Theme.SYSTEM,
    val favoritesSyncPeriod: SyncPeriod = SyncPeriod.OFF,
    val bookmarksSyncPeriod: SyncPeriod = SyncPeriod.OFF,
)
