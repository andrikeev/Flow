package flow.data.api

import flow.models.topic.Torrent

interface TorrentRepository {
    suspend fun loadTorrent(id: String): Torrent
}
