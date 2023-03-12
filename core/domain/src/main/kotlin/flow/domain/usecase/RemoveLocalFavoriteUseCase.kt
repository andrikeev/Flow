package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import javax.inject.Inject

class RemoveLocalFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(id: String) {
        favoritesRepository.removeById(id)
    }
}
