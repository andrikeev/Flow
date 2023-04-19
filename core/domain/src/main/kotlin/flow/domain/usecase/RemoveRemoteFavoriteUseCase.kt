package flow.domain.usecase

import flow.data.api.service.FavoritesService
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoveRemoteFavoriteUseCase @Inject constructor(
    private val favoritesService: FavoritesService,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(id: String) {
        withContext(dispatchers.default) {
            favoritesService.remove(id)
        }
    }
}
