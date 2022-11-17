package flow.downloads.api

interface DownloadService {
    suspend fun downloadTorrentFile(id: String, title: String): String?
}
