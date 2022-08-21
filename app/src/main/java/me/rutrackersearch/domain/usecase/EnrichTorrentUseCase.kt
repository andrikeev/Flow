package me.rutrackersearch.domain.usecase

import me.rutrackersearch.models.topic.Torrent
import me.rutrackersearch.domain.repository.TorrentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnrichTorrentUseCase @Inject constructor(
    private val repository: TorrentRepository,
) {
    suspend operator fun invoke(torrent: Torrent): Torrent {
        val torrentUpdate = repository.loadTorrent(torrent.id)
        return torrent.copy(
            title = torrentUpdate.title,
            author = torrentUpdate.author ?: torrent.author,
            category = torrentUpdate.category ?: torrent.category,
            tags = torrentUpdate.tags ?: torrent.tags,
            status = torrentUpdate.status ?: torrent.status,
            date = torrentUpdate.date ?: torrent.date,
            size = torrentUpdate.size ?: torrent.size,
            seeds = torrentUpdate.seeds ?: torrent.seeds,
            leeches = torrentUpdate.leeches ?: torrent.leeches,
            magnetLink = torrentUpdate.magnetLink ?: torrent.magnetLink,
            description = torrentUpdate.description ?: torrent.description,
        )
    }
}
