package flow.domain.usecase

import flow.data.api.BookmarksRepository
import javax.inject.Inject

class ClearBookmarksUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
) {
    suspend operator fun invoke() {
        bookmarksRepository.clear()
    }
}
