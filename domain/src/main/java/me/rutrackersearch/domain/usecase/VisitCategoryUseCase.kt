package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.repository.BookmarksRepository
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
