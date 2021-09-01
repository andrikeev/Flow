package me.rutrackersearch.domain.repository

import me.rutrackersearch.domain.entity.topic.Torrent

interface TorrentRepository {
    suspend fun loadTorrent(id: String): Torrent
}
