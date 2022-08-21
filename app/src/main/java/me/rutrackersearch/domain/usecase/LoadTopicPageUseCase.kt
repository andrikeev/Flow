package me.rutrackersearch.domain.usecase

import me.rutrackersearch.models.Page
import me.rutrackersearch.models.topic.Post
import me.rutrackersearch.domain.repository.TopicRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadTopicPageUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
) {
    suspend operator fun invoke(id: String, page: Int): Page<Post> {
        return topicRepository.loadCommentsPage(id, page)
    }
}
