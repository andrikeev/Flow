package me.rutrackersearch.domain.service

import me.rutrackersearch.domain.entity.settings.SyncPeriod

interface BookmarksSyncService {
    suspend fun setSyncPeriod(syncPeriod: SyncPeriod)
}
