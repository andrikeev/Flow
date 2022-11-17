package flow.testing.service

import flow.models.settings.SyncPeriod
import flow.work.api.BackgroundService

class TestBackgroundService : BackgroundService {
    override suspend fun addFavoriteTopic(id: String, isTorrent: Boolean) = Unit
    override suspend fun removeFavoriteTopic(id: String) = Unit
    override suspend fun updateBookmark(id: String) = Unit
    override suspend fun loadFavorites() = Unit
    override suspend fun syncFavorites(syncPeriod: SyncPeriod) = Unit
    override suspend fun syncBookmarks(syncPeriod: SyncPeriod) = Unit
    override suspend fun stopBackgroundWorks() = Unit
}
