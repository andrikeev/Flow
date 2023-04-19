package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.data.api.service.TopicService
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddLocalFavoriteUseCase @Inject constructor(
    private val topicService: TopicService,
    private val favoritesRepository: FavoritesRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(id: String) {
        withContext(dispatchers.default) {
            val topic = topicService.getTopic(id)
            favoritesRepository.add(topic)
        }
    }
}
