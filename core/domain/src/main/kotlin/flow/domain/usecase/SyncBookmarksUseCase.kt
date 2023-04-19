package flow.domain.usecase

import flow.data.api.repository.BookmarksRepository
import flow.data.api.service.ForumService
import flow.dispatchers.api.Dispatchers
import flow.notifications.NotificationService
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncBookmarksUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
    private val forumService: ForumService,
    private val notificationService: NotificationService,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke() {
        withContext(dispatchers.default) {
            bookmarksRepository.getAllBookmarks().forEach { bookmark ->
                runCatching {
                    val update = forumService.getCategoryPage(bookmark.id, 1)
                    val updateTopics = update.items.topicsIds()
                    val savedTopics = bookmarksRepository.getTopics(bookmark.id)
                    val savedNewTopics = bookmarksRepository.getNewTopics(bookmark.id)
                    val newTopics = updateTopics
                        .subtract(savedTopics.toSet())
                        .plus(savedNewTopics)
                        .distinct()

                    bookmarksRepository.update(
                        id = bookmark.id,
                        topics = updateTopics,
                        newTopics = newTopics,
                    )
                    if (!savedNewTopics.containsAll(newTopics)) {
                        notificationService.showBookmarkUpdateNotification(bookmark)
                    }
                }
            }
        }
    }
}
