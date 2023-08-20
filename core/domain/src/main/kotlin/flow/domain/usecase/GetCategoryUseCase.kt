package flow.domain.usecase

import flow.data.api.repository.ForumRepository
import flow.dispatchers.api.Dispatchers
import flow.models.forum.Category
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCategoryUseCase @Inject constructor(
    private val ensureForumLoadUseCase: EnsureForumLoadUseCase,
    private val refreshForumUseCase: RefreshForumUseCase,
    private val forumRepository: ForumRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(id: String): Category {
        return withContext(dispatchers.default) {
            ensureForumLoadUseCase()
            val category = forumRepository.getCategory(id)
            if (category == null) {
                refreshForumUseCase.invoke()
            }
            requireNotNull(forumRepository.getCategory(id))
        }
    }
}
