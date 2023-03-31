package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.data.api.service.FavoritesService
import flow.dispatchers.api.Dispatchers
import flow.models.topic.Topic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadFavoritesUseCase @Inject constructor(
    private val favoritesService: FavoritesService,
    private val favoritesRepository: FavoritesRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke() {
        withContext(dispatchers.default) {
            runCatching {
                coroutineScope {
                    val remoteFavoriteTopics = favoritesService.getFavorites()
                    if (remoteFavoriteTopics.isNotEmpty()) {
                        val idsToDelete = favoritesRepository.getIds()
                            .subtract(remoteFavoriteTopics.map(Topic::id).toSet())
                            .toList()
                        favoritesRepository.add(remoteFavoriteTopics)
                        favoritesRepository.removeById(idsToDelete)
                    }
                }
            }
        }
    }
}
