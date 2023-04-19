package flow.domain.usecase

import flow.data.api.repository.BookmarksRepository
import flow.data.api.service.ForumService
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateBookmarkUseCase @Inject constructor(
    private val forumService: ForumService,
    private val bookmarksRepository: BookmarksRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(id: String) {
        withContext(dispatchers.default) {
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
}
