package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.data.api.service.FavoritesService
import flow.data.api.service.TorrentService
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class AddRemoteFavoriteUseCase @Inject constructor(
    private val favoritesService: FavoritesService,
    private val favoritesRepository: FavoritesRepository,
    private val torrentService: TorrentService,
) {
    suspend operator fun invoke(id: String, isTorrent: Boolean) {
        favoritesService.add(id)
        if (isTorrent) {
            runCatching {
                coroutineScope {
                    val torrent = torrentService.getTorrent(id)
                    favoritesRepository.add(torrent)
                }
            }
        }
    }
}
