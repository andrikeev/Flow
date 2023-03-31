package flow.domain.usecase

import flow.data.api.service.TorrentService
import flow.dispatchers.api.Dispatchers
import flow.models.topic.Torrent
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EnrichTorrentUseCase @Inject constructor(
    private val torrentService: TorrentService,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(torrent: Torrent): Torrent {
        return withContext(dispatchers.default) {
            val torrentUpdate = torrentService.getTorrent(torrent.id)
            torrent.copy(
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
}
