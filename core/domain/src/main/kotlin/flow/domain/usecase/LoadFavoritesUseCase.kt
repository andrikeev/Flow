package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.data.api.service.FavoritesService
import flow.models.topic.Topic
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LoadFavoritesUseCase @Inject constructor(
    private val favoritesService: FavoritesService,
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke() {
        runCatching {
            coroutineScope {
                val remoteFavoriteTopics = favoritesService.getFavorites()
                val idsToDelete = favoritesRepository.getIds()
                    .subtract(remoteFavoriteTopics.map(Topic::id).toSet())
                    .toList()
                favoritesRepository.add(remoteFavoriteTopics)
                favoritesRepository.removeById(idsToDelete)
            }
        }
    }
}
