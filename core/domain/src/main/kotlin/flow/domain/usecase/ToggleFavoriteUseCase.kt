package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.dispatchers.api.Dispatchers
import flow.work.api.BackgroundService
import kotlinx.coroutines.withContext

class ToggleFavoriteUseCase(
    private val addLocalFavoriteUseCase: AddLocalFavoriteUseCase,
    private val removeLocalFavoriteUseCase: RemoveLocalFavoriteUseCase,
    private val favoritesRepository: FavoritesRepository,
    private val backgroundService: BackgroundService,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(id: String) {
        withContext(dispatchers.default) {
            val isFavorites = favoritesRepository.contains(id)
            if (isFavorites) {
                removeLocalFavoriteUseCase(id)
                backgroundService.removeFavoriteTopic(id)
            } else {
                addLocalFavoriteUseCase(id)
                backgroundService.addFavoriteTopic(id)
            }
        }
    }
}
