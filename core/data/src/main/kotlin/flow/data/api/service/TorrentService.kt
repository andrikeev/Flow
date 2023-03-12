package flow.data.api.service

import flow.models.topic.Torrent

interface TorrentService {
    suspend fun getTorrent(id: String): Torrent
}
