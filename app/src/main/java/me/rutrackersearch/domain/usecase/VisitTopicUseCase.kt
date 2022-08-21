package me.rutrackersearch.domain.usecase

import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.domain.repository.FavoritesRepository
import me.rutrackersearch.domain.repository.TopicHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitTopicUseCase @Inject constructor(
    private val topicHistoryRepository: TopicHistoryRepository,
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(topic: Topic) {
        topicHistoryRepository.add(topic)
        favoritesRepository.update(topic)
    }
}
