package flow.domain.usecase

import flow.data.api.FavoritesRepository
import flow.data.api.TopicHistoryRepository
import flow.models.topic.Topic
import javax.inject.Inject

class VisitTopicUseCase @Inject constructor(
    private val topicHistoryRepository: TopicHistoryRepository,
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(topic: Topic) {
        topicHistoryRepository.add(topic)
        favoritesRepository.update(topic)
    }
}
