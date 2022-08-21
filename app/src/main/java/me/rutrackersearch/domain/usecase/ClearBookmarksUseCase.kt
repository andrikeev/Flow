package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.repository.BookmarksRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearBookmarksUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
) {
    suspend operator fun invoke() {
        bookmarksRepository.clear()
    }
}
