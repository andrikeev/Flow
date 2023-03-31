package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.data.api.repository.VisitedRepository
import flow.dispatchers.api.Dispatchers
import flow.models.topic.Topic
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VisitTopicUseCase @Inject constructor(
    private val visitedRepository: VisitedRepository,
    private val favoritesRepository: FavoritesRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(topic: Topic) {
        withContext(dispatchers.default) {
            favoritesRepository.update(topic)
            visitedRepository.add(topic)
        }
    }
}
