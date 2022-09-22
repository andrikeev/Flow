package me.rutrackersearch.data.repository

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rutrackersearch.data.database.converters.Converters
import me.rutrackersearch.data.database.dao.BookmarkDao
import me.rutrackersearch.data.database.entity.BookmarkEntity
import me.rutrackersearch.data.workers.UpdateBookmarkWorker
import me.rutrackersearch.data.workers.oneTimeWorkRequest
import me.rutrackersearch.domain.repository.BookmarksRepository
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.forum.CategoryModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarksRepositoryImpl @Inject constructor(
    private val dao: BookmarkDao,
    private val workManager: WorkManager,
) : BookmarksRepository {
    override fun observeBookmarks(): Flow<List<CategoryModel>> {
        return dao.observerAll().map { entities ->
            entities.map { entity ->
                CategoryModel(
                    category = entity.category,
                    isBookmark = true,
                    newTopicsCount = entity.newTopics.size,
                )
            }
        }
    }

    override fun observeIds(): Flow<List<String>> {
        return dao.observerIds()
    }

    override fun observeNewTopics(): Flow<List<String>> {
        return dao.observeNewTopics()
            .map { it.map { ids -> Converters.toStringList(ids) }.flatten() }
    }

    override fun observeNewTopics(id: String): Flow<List<String>> {
        return dao.observeNewTopics(id)
    }

    override suspend fun add(category: Category) {
        dao.insert(BookmarkEntity.of(category))
        updateBookmark(category.id)
    }

    override suspend fun remove(category: Category) {
        dao.deleteById(category.id)
    }

    override suspend fun clear() {
        dao.deleteAll()
    }

    override suspend fun markVisited(id: String) {
        updateBookmark(id)
    }

    private fun updateBookmark(id: String) {
        val inputData = UpdateBookmarkWorker.dataOf(id)
        val workRequest = oneTimeWorkRequest<UpdateBookmarkWorker>(inputData)
        workManager.enqueueUniqueWork(id, ExistingWorkPolicy.KEEP, workRequest)
    }
}
