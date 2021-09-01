package me.rutrackersearch.data.repository

import me.rutrackersearch.data.converters.parseTopic
import me.rutrackersearch.data.converters.readJson
import me.rutrackersearch.data.converters.toFailure
import me.rutrackersearch.data.network.ServerApi
import me.rutrackersearch.domain.entity.topic.Torrent
import me.rutrackersearch.domain.repository.TorrentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TorrentRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : TorrentRepository {

    override suspend fun loadTorrent(id: String): Torrent {
        return try {
            api.torrent(id).readJson().parseTopic() as Torrent
        } catch (e: Exception) {
            throw e.toFailure()
        }
    }
}
