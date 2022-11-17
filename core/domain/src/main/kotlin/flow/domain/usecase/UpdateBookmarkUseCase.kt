package flow.domain.usecase

import flow.data.api.BookmarksRepository
import flow.models.forum.CategoryModel
import javax.inject.Inject

class UpdateBookmarkUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
) {
    suspend operator fun invoke(category: CategoryModel) {
        if (category.isBookmark) {
            bookmarksRepository.remove(category.category)
        } else {
            bookmarksRepository.add(category.category)
        }
    }
}
