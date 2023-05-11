package flow.domain.usecase

import flow.data.api.service.TopicService
import flow.dispatchers.api.Dispatchers
import flow.models.topic.TopicPage
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTopicUseCase @Inject constructor(
    private val topicService: TopicService,
    private val visitTopicUseCase: VisitTopicUseCase,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(id: String): TopicPage {
        return withContext(dispatchers.default) {
            topicService.getTopicPage(id).also {
                visitTopicUseCase(it)
            }
        }
    }
}
