package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import javax.inject.Inject

class ClearLocalFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke() {
        favoritesRepository.clear()
    }
}
