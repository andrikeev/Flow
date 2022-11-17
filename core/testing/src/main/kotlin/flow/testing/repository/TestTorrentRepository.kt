package flow.testing.repository

import flow.data.api.TorrentRepository
import flow.models.topic.Torrent

class TestTorrentRepository : TorrentRepository {
    override suspend fun loadTorrent(id: String): Torrent {
        TODO("Not yet implemented")
    }
}
