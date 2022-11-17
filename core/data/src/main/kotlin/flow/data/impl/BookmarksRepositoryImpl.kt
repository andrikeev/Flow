package flow.data.impl

import flow.data.api.BookmarksRepository
import flow.database.dao.BookmarkDao
import flow.database.entity.BookmarkEntity
import flow.models.forum.Category
import flow.models.forum.CategoryModel
import flow.work.api.BackgroundService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import javax.inject.Inject

class BookmarksRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao,
    private val backgroundService: BackgroundService,
) : BookmarksRepository {
    override fun observeBookmarks(): Flow<List<CategoryModel>> {
        return bookmarkDao.observerAll().map { entities ->
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
        return bookmarkDao.observerIds()
    }

    override fun observeNewTopics(): Flow<List<String>> {
        return bookmarkDao.observeNewTopics()
            .map {
                it.map { ids ->
                    JSONArray(ids).run {
                        IntRange(0, length() - 1).map { index ->
                            getString(index)
                        }
                    }
                }.flatten()
            }
    }

    override fun observeNewTopics(id: String): Flow<List<String>> {
        return bookmarkDao.observeNewTopics(id)
    }

    override suspend fun add(category: Category) {
        bookmarkDao.insert(BookmarkEntity.of(category))
        backgroundService.updateBookmark(category.id)
    }

    override suspend fun remove(category: Category) {
        bookmarkDao.deleteById(category.id)
    }

    override suspend fun clear() {
        bookmarkDao.deleteAll()
    }

    override suspend fun markVisited(id: String) {
        backgroundService.updateBookmark(id)
    }
}
