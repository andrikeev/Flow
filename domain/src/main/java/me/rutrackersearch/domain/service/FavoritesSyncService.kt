package me.rutrackersearch.domain.service

import me.rutrackersearch.domain.entity.settings.SyncPeriod

interface FavoritesSyncService {
    suspend fun setSyncPeriod(syncPeriod: SyncPeriod)
}
