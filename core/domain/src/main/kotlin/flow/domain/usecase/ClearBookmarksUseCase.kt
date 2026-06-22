package flow.domain.usecase

import flow.data.api.repository.BookmarksRepository
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext

class ClearBookmarksUseCase(
    private val bookmarksRepository: BookmarksRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke() {
        withContext(dispatchers.default) {
            bookmarksRepository.clear()
        }
    }
}
