package me.rutrackersearch.domain.service

interface TorrentDownloadService {
    suspend fun downloadTorrent(id: String, title: String): String?
}