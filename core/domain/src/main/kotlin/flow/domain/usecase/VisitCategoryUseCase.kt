package flow.domain.usecase

import flow.data.api.repository.BookmarksRepository
import flow.models.forum.Category
import flow.models.topic.Topic
import javax.inject.Inject

class VisitCategoryUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
) {
    suspend operator fun invoke(category: Category, topics: List<Topic>) {
        bookmarksRepository.update(
            id = category.id,
            topics = topics.map(Topic::id),
            newTopics = emptyList(),
        )
    }
}
