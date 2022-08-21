package me.rutrackersearch.domain.service

import me.rutrackersearch.models.settings.SyncPeriod

interface FavoritesSyncService {
    suspend fun setSyncPeriod(syncPeriod: SyncPeriod)
}
