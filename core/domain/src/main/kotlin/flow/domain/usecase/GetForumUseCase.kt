package flow.domain.usecase

import flow.data.api.service.ForumService
import flow.models.forum.Forum
import flow.models.forum.ForumTreeGroup
import flow.models.forum.RootCategory
import javax.inject.Inject

class GetForumUseCase @Inject constructor(
    private val forumService: ForumService,
) {
    suspend operator fun invoke(): Forum {
        val forumTree = forumService.loadForumTree()
        return Forum(
            forumTree.children.map { forumTreeRootGroup ->
                RootCategory(
                    name = forumTreeRootGroup.name,
                    children = forumTreeRootGroup.children.map(ForumTreeGroup::category)
                )
            }
        )
    }
}
