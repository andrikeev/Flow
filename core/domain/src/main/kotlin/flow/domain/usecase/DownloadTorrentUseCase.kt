package flow.domain.usecase

import flow.auth.api.TokenProvider
import flow.dispatchers.api.Dispatchers
import flow.downloads.api.DownloadRequest
import flow.downloads.api.DownloadService
import flow.models.topic.Torrent
import flow.network.data.NetworkApiRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DownloadTorrentUseCase @Inject constructor(
    private val networkApiRepository: NetworkApiRepository,
    private val downloadService: DownloadService,
    private val tokenProvider: TokenProvider,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(torrent: Torrent): String? {
        return withContext(dispatchers.default) {
            val token = tokenProvider.getToken()
            downloadService.downloadTorrentFile(
                DownloadRequest(
                    id = torrent.id,
                    title = torrent.title,
                    uri = networkApiRepository.getDownloadUri(torrent.id),
                    headers = listOf(networkApiRepository.getAuthHeader(token)),
                )
            )
        }
    }
}
