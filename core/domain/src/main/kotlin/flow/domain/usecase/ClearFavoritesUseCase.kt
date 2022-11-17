package flow.domain.usecase

import flow.data.api.FavoritesRepository
import javax.inject.Inject

class ClearFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke() {
        favoritesRepository.clear()
    }
}
