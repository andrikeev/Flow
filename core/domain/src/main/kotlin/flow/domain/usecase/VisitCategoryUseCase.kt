package flow.domain.usecase

import flow.data.api.BookmarksRepository
import flow.models.forum.Category
import javax.inject.Inject

class VisitCategoryUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
) {
    suspend operator fun invoke(category: Category) {
        bookmarksRepository.markVisited(category.id)
    }
}
