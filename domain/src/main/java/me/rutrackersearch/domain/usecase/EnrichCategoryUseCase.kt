package me.rutrackersearch.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.rutrackersearch.domain.entity.CategoryModel
import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.repository.BookmarksRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnrichCategoryUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
) {
    operator fun invoke(category: Category): Flow<CategoryModel> {
        return combine(
            bookmarksRepository.observeIds(),
            bookmarksRepository.observeNewTopics(category.id),
        ) { bookmarks, newTopics ->
            CategoryModel(
                data = category,
                isBookmark = bookmarks.contains(category.id),
                newTopicsCount = newTopics.size,
            )
        }
    }
}
