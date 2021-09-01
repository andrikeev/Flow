package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.entity.CategoryModel
import me.rutrackersearch.domain.repository.BookmarksRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateBookmarkUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
) {
    suspend operator fun invoke(category: CategoryModel) {
        if (category.isBookmark) {
            bookmarksRepository.remove(category.data)
        } else {
            bookmarksRepository.add(category.data)
        }
    }
}
