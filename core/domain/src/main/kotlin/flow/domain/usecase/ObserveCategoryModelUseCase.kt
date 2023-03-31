package flow.domain.usecase

import flow.data.api.repository.BookmarksRepository
import flow.models.forum.CategoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveCategoryModelUseCase @Inject constructor(
    private val getCategoryUseCase: GetCategoryUseCase,
    private val bookmarksRepository: BookmarksRepository,
) {
    suspend operator fun invoke(categoryId: String): Flow<CategoryModel> {
        val category = getCategoryUseCase(categoryId)
        return combine(
            bookmarksRepository.observeIds(),
            bookmarksRepository.observeNewTopics(categoryId),
        ) { bookmarks, newTopics ->
            CategoryModel(
                category = category,
                isBookmark = bookmarks.contains(categoryId),
                newTopicsCount = newTopics.size,
            )
        }
    }
}
