package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository

class RemoveLocalFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(id: String) {
        favoritesRepository.removeById(id)
    }
}
