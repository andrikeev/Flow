package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.data.api.service.TorrentService
import flow.notifications.NotificationService
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SyncFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val torrentService: TorrentService,
    private val notificationService: NotificationService,
) {
    suspend operator fun invoke() {
        favoritesRepository.getTorrents().forEach { torrent ->
            runCatching {
                coroutineScope {
                    val update = torrentService.getTorrent(torrent.id)
                    val hasUpdate = torrent.magnetLink != null &&
                            update.magnetLink != null &&
                            torrent.magnetLink != update.magnetLink
                    val updated = torrent.copy(
                        title = update.title,
                        author = update.author,
                        category = update.category,
                        tags = update.tags,
                        status = update.status,
                        date = update.date,
                        size = update.size,
                        seeds = update.seeds,
                        leeches = update.leeches,
                        magnetLink = update.magnetLink,
                    )
                    favoritesRepository.updateTorrent(updated, hasUpdate)
                    if (hasUpdate) {
                        notificationService.showFavoriteUpdateNotification(updated)
                    }
                }
            }
        }
    }
}
