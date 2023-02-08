package flow.domain.usecase

import flow.data.api.service.TopicService
import flow.models.Page
import flow.models.topic.Post
import javax.inject.Inject

class GetTopicPageUseCase @Inject constructor(
    private val topicService: TopicService,
) {
    suspend operator fun invoke(id: String, page: Int): Page<Post> {
        return topicService.getCommentsPage(id, page)
    }
}
