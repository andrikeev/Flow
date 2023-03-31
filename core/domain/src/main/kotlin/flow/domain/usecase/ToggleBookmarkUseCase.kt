package flow.domain.usecase

import flow.data.api.repository.BookmarksRepository
import flow.dispatchers.api.Dispatchers
import flow.work.api.BackgroundService
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
    private val backgroundService: BackgroundService,
    private val getCategoryUseCase: GetCategoryUseCase,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(categoryId: String) {
        withContext(dispatchers.default) {
            if (bookmarksRepository.isBookmark(categoryId)) {
                bookmarksRepository.remove(categoryId)
            } else {
                bookmarksRepository.add(getCategoryUseCase(categoryId))
                backgroundService.updateBookmark(categoryId)
            }
        }
    }
}
