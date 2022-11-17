package flow.domain.usecase

import flow.models.forum.Forum
import flow.models.forum.ForumTreeGroup
import flow.models.forum.RootCategory
import javax.inject.Inject

class LoadForumUseCase @Inject constructor(
    private val forumRepository: flow.data.api.ForumRepository,
) {
    suspend operator fun invoke(): Forum {
        val forumTree = forumRepository.loadForumTree()
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
