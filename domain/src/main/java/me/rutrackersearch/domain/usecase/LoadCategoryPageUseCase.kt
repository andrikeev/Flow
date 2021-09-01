package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.entity.Page
import me.rutrackersearch.domain.entity.forum.ForumItem
import me.rutrackersearch.domain.repository.ForumRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadCategoryPageUseCase @Inject constructor(
    private val forumRepository: ForumRepository
) {
    suspend operator fun invoke(id: String, page: Int): Page<ForumItem> {
        return forumRepository.loadCategoryPage(id, page)
    }
}
