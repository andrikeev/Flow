package flow.domain.usecase

import flow.data.api.repository.BookmarksRepository
import flow.models.forum.CategoryModel
import flow.work.api.BackgroundService
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
    private val backgroundService: BackgroundService,
) {
    suspend operator fun invoke(model: CategoryModel) {
        if (model.isBookmark) {
            bookmarksRepository.remove(model.category)
        } else {
            bookmarksRepository.add(model.category)
            backgroundService.updateBookmark(model.category.id)
        }
    }
}
