package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveFavoriteStateUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(id: String): Flow<Boolean> = favoritesRepository.observeIds().map { it.contains(id) }
}
