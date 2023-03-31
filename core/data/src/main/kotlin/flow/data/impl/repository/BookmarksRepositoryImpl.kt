package flow.data.impl.repository

import flow.data.api.repository.BookmarksRepository
import flow.data.converters.toBookmarkEntity
import flow.database.dao.BookmarkDao
import flow.database.entity.BookmarkEntity
import flow.models.forum.Category
import flow.models.forum.CategoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import javax.inject.Inject

class BookmarksRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao,
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
                        IntRange(0, length() - 1).map(::getString)
                    }
                }.flatten()
            }
    }

    override fun observeNewTopics(id: String): Flow<List<String>> {
        return bookmarkDao.observeNewTopics(id)
    }

    override suspend fun getAllBookmarks(): List<Category> {
        return bookmarkDao.getAll().map(BookmarkEntity::category)
    }

    override suspend fun getTopics(id: String): List<String> {
        return bookmarkDao.get(id)?.topics ?: emptyList()
    }

    override suspend fun getNewTopics(id: String): List<String> {
        return bookmarkDao.get(id)?.newTopics ?: emptyList()
    }

    override suspend fun isBookmark(id: String): Boolean {
        return bookmarkDao.contains(id)
    }

    override suspend fun add(category: Category) {
        bookmarkDao.insert(category.toBookmarkEntity())
    }

    override suspend fun update(id: String, topics: List<String>, newTopics: List<String>) {
        bookmarkDao.get(id)?.let { entity ->
            bookmarkDao.insert(
                entity.copy(
                    topics = topics,
                    newTopics = newTopics,
                )
            )
        }
    }

    override suspend fun remove(id: String) {
        bookmarkDao.deleteById(id)
    }

    override suspend fun clear() {
        bookmarkDao.deleteAll()
    }
}
