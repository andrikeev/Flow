package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.repository.ForumRepository
import me.rutrackersearch.models.forum.Forum
import me.rutrackersearch.models.forum.ForumTreeGroup
import me.rutrackersearch.models.forum.RootCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadForumUseCase @Inject constructor(
    private val forumRepository: ForumRepository,
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
