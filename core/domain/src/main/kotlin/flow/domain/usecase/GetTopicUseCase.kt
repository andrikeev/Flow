package flow.domain.usecase

import flow.data.api.service.TopicService
import flow.models.topic.Topic
import javax.inject.Inject

class GetTopicUseCase @Inject constructor(
    private val topicService: TopicService,
) {
    suspend operator fun invoke(id: String, pid: String): Topic {
        return topicService.getTopic(id, pid)
    }
}
