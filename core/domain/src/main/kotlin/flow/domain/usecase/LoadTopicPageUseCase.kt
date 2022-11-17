package flow.domain.usecase

import flow.data.api.TopicRepository
import flow.models.Page
import flow.models.topic.Post
import javax.inject.Inject

class LoadTopicPageUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
) {
    suspend operator fun invoke(id: String, page: Int): Page<Post> {
        return topicRepository.loadCommentsPage(id, page)
    }
}
