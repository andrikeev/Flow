package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.service.TorrentDownloadService
import me.rutrackersearch.models.topic.Torrent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadTorrentUseCase @Inject constructor(
    private val downloadService: TorrentDownloadService,
) {
    suspend operator fun invoke(torrent: Torrent): String? {
        return downloadService.downloadTorrent(torrent.id, torrent.title)
    }
}
