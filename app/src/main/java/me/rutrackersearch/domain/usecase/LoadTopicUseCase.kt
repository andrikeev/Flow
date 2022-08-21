package me.rutrackersearch.domain.usecase

import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.domain.repository.TopicRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadTopicUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
) {
    suspend operator fun invoke(id: String, pid: String): Topic {
        return topicRepository.loadTopic(id, pid)
    }
}
