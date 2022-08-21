package me.rutrackersearch.data.repository

import me.rutrackersearch.domain.repository.TorrentRepository
import me.rutrackersearch.models.topic.Torrent
import me.rutrackersearch.network.NetworkApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TorrentRepositoryImpl @Inject constructor(
    private val api: NetworkApi,
) : TorrentRepository {
    override suspend fun loadTorrent(id: String): Torrent {
        return api.torrent(id)
    }
}
