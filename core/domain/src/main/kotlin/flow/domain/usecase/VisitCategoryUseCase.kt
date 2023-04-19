package flow.domain.usecase

import flow.data.api.repository.BookmarksRepository
import flow.dispatchers.api.Dispatchers
import flow.models.topic.Topic
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VisitCategoryUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(categoryId: String, topics: List<Topic>) {
        withContext(dispatchers.default) {
            if (bookmarksRepository.isBookmark(categoryId)) {
                bookmarksRepository.update(
                    id = categoryId,
                    topics = topics.map(Topic::id),
                    newTopics = emptyList(),
                )
            }
        }
    }
}
