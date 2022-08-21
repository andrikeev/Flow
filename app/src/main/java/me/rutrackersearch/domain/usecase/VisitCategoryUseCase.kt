package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.repository.BookmarksRepository
import me.rutrackersearch.models.forum.Category
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitCategoryUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
) {
    suspend operator fun invoke(category: Category) {
        bookmarksRepository.markVisited(category.id)
    }
}
