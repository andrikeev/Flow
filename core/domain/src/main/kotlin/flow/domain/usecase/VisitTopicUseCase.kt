package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.data.api.repository.VisitedRepository
import flow.models.topic.Topic
import javax.inject.Inject

class VisitTopicUseCase @Inject constructor(
    private val visitedRepository: VisitedRepository,
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(topic: Topic) {
        favoritesRepository.update(topic)
        visitedRepository.add(topic)
    }
}
