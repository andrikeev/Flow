package me.rutrackersearch.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import me.rutrackersearch.domain.entity.CategoryModel
import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.repository.BookmarksRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnrichCategoriesUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
) {
    operator fun invoke(categories: List<Category>): Flow<List<CategoryModel>> {
        return bookmarksRepository.observeIds()
            .mapLatest { bookmarks ->
                categories.map { category ->
                    CategoryModel(
                        data = category,
                        isBookmark = bookmarks.contains(category.id),
                    )
                }
            }
    }
}
