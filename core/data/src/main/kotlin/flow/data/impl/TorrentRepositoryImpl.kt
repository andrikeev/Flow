package flow.data.impl

import flow.data.api.TorrentRepository
import flow.models.topic.Torrent
import flow.network.NetworkApi
import javax.inject.Inject

class TorrentRepositoryImpl @Inject constructor(
    private val networkApi: NetworkApi,
) : TorrentRepository {
    override suspend fun loadTorrent(id: String): Torrent {
        return networkApi.torrent(id)
    }
}
