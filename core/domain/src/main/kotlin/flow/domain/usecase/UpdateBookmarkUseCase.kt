package flow.domain.usecase

import flow.data.api.repository.BookmarksRepository
import flow.data.api.service.ForumService
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class UpdateBookmarkUseCase @Inject constructor(
    private val forumService: ForumService,
    private val bookmarksRepository: BookmarksRepository,
) {
    suspend operator fun invoke(id: String) {
        runCatching {
            coroutineScope {
                val category = forumService.getCategoryPage(id, 1)
                bookmarksRepository.update(
                    id = id,
                    topics = category.items.topicsIds(),
                    newTopics = emptyList(),
                )
            }
        }
    }
}
