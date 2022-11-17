package flow.testing.service

import flow.downloads.api.DownloadService

class TestDownloadService : DownloadService {
    override suspend fun downloadTorrentFile(id: String, title: String): String? {
        TODO("Not yet implemented")
    }
}
