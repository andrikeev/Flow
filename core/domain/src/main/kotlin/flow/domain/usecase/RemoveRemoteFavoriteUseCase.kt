package flow.domain.usecase

import flow.data.api.service.FavoritesService
import javax.inject.Inject

class RemoveRemoteFavoriteUseCase @Inject constructor(
    private val favoritesService: FavoritesService,
) {
    suspend operator fun invoke(id: String) {
        favoritesService.remove(id)
    }
}
