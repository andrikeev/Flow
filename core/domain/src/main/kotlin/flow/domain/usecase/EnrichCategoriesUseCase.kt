package flow.domain.usecase

import flow.data.api.BookmarksRepository
import flow.models.forum.Category
import flow.models.forum.CategoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class EnrichCategoriesUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
) {
    operator fun invoke(categories: List<Category>): Flow<List<CategoryModel>> {
        return bookmarksRepository.observeIds()
            .mapLatest { bookmarks ->
                categories.map { category ->
                    CategoryModel(
                        category = category,
                        isBookmark = bookmarks.contains(category.id),
                    )
                }
            }
    }
}
