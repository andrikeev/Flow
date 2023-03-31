package flow.models.settings

data class Settings(
    val endpoint: Endpoint = Endpoint.Proxy,
    val theme: Theme = Theme.SYSTEM,
    val favoritesSyncPeriod: SyncPeriod = SyncPeriod.OFF,
    val bookmarksSyncPeriod: SyncPeriod = SyncPeriod.OFF,
)
