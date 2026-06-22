package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveFavoriteStateUseCase(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(id: String): Flow<Boolean> {
        return favoritesRepository.observeIds().map { it.contains(id) }
    }
}
