package me.rutrackersearch.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import me.rutrackersearch.data.database.AppDatabase
import me.rutrackersearch.data.database.entity.BookmarkEntity
import me.rutrackersearch.data.notifications.NotificationService
import me.rutrackersearch.domain.entity.forum.ForumTopic
import me.rutrackersearch.domain.entity.topic.Topic
import me.rutrackersearch.domain.repository.BookmarksRepository
import me.rutrackersearch.domain.repository.ForumRepository

@HiltWorker
class SyncBookmarksWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookmarksRepository: BookmarksRepository,
    private val foreRepository: ForumRepository,
    private val notificationServices: NotificationService,
    db: AppDatabase,
) : CoroutineWorker(appContext, workerParams) {

    private val dao = db.bookmarkDao()

    override suspend fun doWork(): Result {
        return try {
            val bookmarks = bookmarksRepository.observeBookmarks().first()
            bookmarks.forEach { bookmark ->
                val id = bookmark.data.id
                val oldTopics = dao.getTopics(id)
                val updateTopics = foreRepository
                    .loadCategoryPage(id, 1)
                    .items
                    .filterIsInstance<ForumTopic>()
                    .map(ForumTopic::topic)
                    .map(Topic::id)

                val newTopics = updateTopics.filterNot(oldTopics::contains)
                dao.insert(
                    BookmarkEntity.of(bookmark.data).copy(
                        topics = updateTopics,
                        newTopics = newTopics,
                    )
                )
                if (newTopics.isNotEmpty()) {
                    notificationServices.showBookmarkUpdateNotification(
                        bookmark.copy(newTopicsCount = newTopics.size)
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
