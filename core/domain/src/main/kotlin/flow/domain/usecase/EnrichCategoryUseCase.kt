package flow.domain.usecase

import flow.data.api.repository.BookmarksRepository
import flow.models.forum.Category
import flow.models.forum.CategoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class EnrichCategoryUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
) {
    operator fun invoke(category: Category): Flow<CategoryModel> {
        return combine(
            bookmarksRepository.observeIds(),
            bookmarksRepository.observeNewTopics(category.id),
        ) { bookmarks, newTopics ->
            CategoryModel(
                category = category,
                isBookmark = bookmarks.contains(category.id),
                newTopicsCount = newTopics.size,
            )
        }
    }
}
