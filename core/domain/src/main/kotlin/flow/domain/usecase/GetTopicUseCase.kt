package flow.domain.usecase

import flow.data.api.service.TopicService
import flow.dispatchers.api.Dispatchers
import flow.models.topic.Topic
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTopicUseCase @Inject constructor(
    private val topicService: TopicService,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(id: String): Topic {
        return withContext(dispatchers.default) {
            topicService.getTopic(id)
        }
    }
}
