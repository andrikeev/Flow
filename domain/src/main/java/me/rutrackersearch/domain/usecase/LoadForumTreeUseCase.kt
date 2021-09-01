package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.entity.forum.ForumTree
import me.rutrackersearch.domain.repository.ForumRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadForumTreeUseCase @Inject constructor(
    private val forumRepository: ForumRepository,
) {
    suspend fun invoke(): ForumTree {
        return forumRepository.loadForumTree()
    }
}
