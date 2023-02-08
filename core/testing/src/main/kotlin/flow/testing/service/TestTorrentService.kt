package flow.testing.service

import flow.data.api.service.TorrentService
import flow.models.topic.Torrent

class TestTorrentService : TorrentService {
    override suspend fun getTorrent(id: String): Torrent {
        TODO("Not yet implemented")
    }
}
