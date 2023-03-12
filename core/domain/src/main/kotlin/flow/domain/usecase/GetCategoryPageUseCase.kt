package flow.domain.usecase

import flow.data.api.service.ForumService
import flow.models.Page
import flow.models.forum.ForumItem
import javax.inject.Inject

class GetCategoryPageUseCase @Inject constructor(
    private val forumService: ForumService,
) {
    suspend operator fun invoke(id: String, page: Int): Page<ForumItem> {
        return forumService.getCategoryPage(id, page)
    }
}
