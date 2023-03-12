package flow.data.impl.service

import flow.auth.api.TokenProvider
import flow.data.api.service.TorrentService
import flow.data.converters.toTorrent
import flow.models.topic.Torrent
import flow.network.api.NetworkApi
import javax.inject.Inject

class TorrentServiceImpl @Inject constructor(
    private val networkApi: NetworkApi,
    private val tokenProvider: TokenProvider,
) : TorrentService {
    override suspend fun getTorrent(id: String): Torrent {
        return networkApi.getTorrent(tokenProvider.getToken(), id).toTorrent()
    }
}
