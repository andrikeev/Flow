package me.rutrackersearch.domain.service

import me.rutrackersearch.models.settings.SyncPeriod

interface BookmarksSyncService {
    suspend fun setSyncPeriod(syncPeriod: SyncPeriod)
}
