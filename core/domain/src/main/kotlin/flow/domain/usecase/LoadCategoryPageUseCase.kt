package flow.domain.usecase

import flow.data.api.ForumRepository
import flow.models.Page
import flow.models.forum.ForumItem
import javax.inject.Inject

class LoadCategoryPageUseCase @Inject constructor(
    private val forumRepository: ForumRepository,
) {
    suspend operator fun invoke(id: String, page: Int): Page<ForumItem> {
        return forumRepository.loadCategoryPage(id, page)
    }
}
