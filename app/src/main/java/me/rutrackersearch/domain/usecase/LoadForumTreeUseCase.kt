package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.repository.ForumRepository
import me.rutrackersearch.models.forum.ForumTree
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadForumTreeUseCase @Inject constructor(
    private val forumRepository: ForumRepository,
) {
    suspend operator fun invoke(): ForumTree {
        return forumRepository.loadForumTree()
    }
}
