package flow.domain.usecase

import flow.data.api.ForumRepository
import flow.models.forum.ForumTree
import javax.inject.Inject

class GetForumTreeUseCase @Inject constructor(
    private val forumRepository: ForumRepository,
) {
    suspend operator fun invoke(): ForumTree {
        return forumRepository.loadForumTree()
    }
}
