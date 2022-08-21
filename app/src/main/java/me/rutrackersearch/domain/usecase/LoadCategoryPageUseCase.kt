package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.repository.ForumRepository
import me.rutrackersearch.models.Page
import me.rutrackersearch.models.forum.ForumItem
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
