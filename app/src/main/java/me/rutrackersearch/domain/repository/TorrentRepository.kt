package me.rutrackersearch.domain.repository

import me.rutrackersearch.models.topic.Torrent

interface TorrentRepository {
    suspend fun loadTorrent(id: String): Torrent
}
