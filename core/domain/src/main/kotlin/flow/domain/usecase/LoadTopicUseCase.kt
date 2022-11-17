package flow.domain.usecase

import flow.data.api.TopicRepository
import flow.models.topic.Topic
import javax.inject.Inject

class LoadTopicUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
) {
    suspend operator fun invoke(id: String, pid: String): Topic {
        return topicRepository.loadTopic(id, pid)
    }
}
