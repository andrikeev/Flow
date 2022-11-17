package flow.domain.usecase

import flow.data.api.TopicRepository
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
) {
    suspend operator fun invoke(topicId: String, message: String) {
        topicRepository.addComment(topicId, message)
    }
}
