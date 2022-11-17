package flow.domain.usecase

import flow.downloads.api.DownloadService
import flow.models.topic.Torrent
import javax.inject.Inject

class DownloadTorrentUseCase @Inject constructor(
    private val downloadService: DownloadService,
) {
    suspend operator fun invoke(torrent: Torrent): String? {
        return downloadService.downloadTorrentFile(torrent.id, torrent.title)
    }
}
