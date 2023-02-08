package flow.domain.usecase

import flow.data.api.service.ForumService
import flow.models.forum.ForumTree
import javax.inject.Inject

class GetForumTreeUseCase @Inject constructor(
    private val forumService: ForumService,
) {
    suspend operator fun invoke(): ForumTree {
        return forumService.loadForumTree()
    }
}
