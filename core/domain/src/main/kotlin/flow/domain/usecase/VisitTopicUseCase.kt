package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.data.api.repository.VisitedRepository
import flow.dispatchers.api.Dispatchers
import flow.models.topic.TopicPage
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VisitTopicUseCase @Inject constructor(
    private val visitedRepository: VisitedRepository,
    private val favoritesRepository: FavoritesRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(topic: TopicPage) {
        withContext(dispatchers.default) {
            favoritesRepository.markVisited(topic.id)
            visitedRepository.add(topic)
        }
    }
}
