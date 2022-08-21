package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.repository.TopicRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddCommentUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
) {
    suspend operator fun invoke(topicId: String, message: String) {
        topicRepository.addComment(topicId, message)
    }
}
