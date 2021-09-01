package me.rutrackersearch.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rutrackersearch.data.database.AppDatabase
import me.rutrackersearch.data.database.converters.Converters
import me.rutrackersearch.data.database.entity.BookmarkEntity
import me.rutrackersearch.domain.entity.CategoryModel
import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.repository.BookmarksRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarksRepositoryImpl @Inject constructor(
    db: AppDatabase
) : BookmarksRepository {
    private val dao = db.bookmarkDao()

    override fun observeBookmarks(): Flow<List<CategoryModel>> {
        return dao.observerAll().map { entities ->
            entities.map { entity ->
                CategoryModel(
                    data = entity.category,
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
    }

    override suspend fun remove(category: Category) {
        dao.deleteById(category.id)
    }

    override suspend fun clear() {
        dao.deleteAll()
    }

    override suspend fun markVisited(id: String) {
        dao.markVisited(id)
    }
}
