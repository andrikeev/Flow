package flow.work.api

import flow.models.settings.SyncPeriod

interface BackgroundService {
    suspend fun addFavoriteTopic(id: String)
    suspend fun removeFavoriteTopic(id: String)
    suspend fun updateBookmark(id: String)
    suspend fun loadFavorites()
    suspend fun syncFavorites(syncPeriod: SyncPeriod)
    suspend fun syncBookmarks(syncPeriod: SyncPeriod)
    suspend fun stopBackgroundWorks()
}
