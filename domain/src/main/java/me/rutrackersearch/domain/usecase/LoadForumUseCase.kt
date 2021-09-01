package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.entity.forum.Forum
import me.rutrackersearch.domain.entity.forum.ForumTreeGroup
import me.rutrackersearch.domain.entity.forum.RootCategory
import me.rutrackersearch.domain.repository.ForumRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadForumUseCase @Inject constructor(
    private val forumRepository: ForumRepository,
) {
    suspend fun invoke(): Forum {
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
