package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClearLocalFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke() {
        withContext(dispatchers.default) {
            favoritesRepository.clear()
        }
    }
}
