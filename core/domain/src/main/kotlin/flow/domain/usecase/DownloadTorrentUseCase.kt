package flow.domain.usecase

import flow.dispatchers.api.Dispatchers
import flow.downloads.api.DownloadService
import flow.models.topic.Torrent
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DownloadTorrentUseCase @Inject constructor(
    private val downloadService: DownloadService,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(torrent: Torrent): String? {
        return withContext(dispatchers.default) {
            downloadService.downloadTorrentFile (torrent.id, torrent.title)
        }
    }
}
