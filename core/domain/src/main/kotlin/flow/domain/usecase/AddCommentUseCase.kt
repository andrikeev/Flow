package flow.domain.usecase

import flow.data.api.service.TopicService
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(
    private val topicService: TopicService,
) {
    suspend operator fun invoke(topicId: String, message: String) = kotlin.runCatching{
        topicService.addComment(topicId, message)
    }.isSuccess
}
