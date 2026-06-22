package flow.domain.usecase

import flow.data.api.service.TopicService
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext

class AddCommentUseCase(
    private val topicService: TopicService,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(topicId: String, message: String): Boolean {
        return withContext(dispatchers.default) {
            runCatching {
                topicService.addComment(topicId, message)
            }.isSuccess
        }
    }
}
