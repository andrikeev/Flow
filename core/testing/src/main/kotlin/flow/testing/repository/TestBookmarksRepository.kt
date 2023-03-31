package flow.testing.repository

import flow.data.api.repository.BookmarksRepository
import flow.models.forum.Category
import flow.models.forum.CategoryModel
import kotlinx.coroutines.flow.Flow

class TestBookmarksRepository : BookmarksRepository {
    override fun observeBookmarks(): Flow<List<CategoryModel>> {
        TODO("Not yet implemented")
    }

    override fun observeIds(): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override fun observeNewTopics(): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override fun observeNewTopics(id: String): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllBookmarks(): List<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun getTopics(id: String): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getNewTopics(id: String): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun isBookmark(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun add(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun update(id: String, topics: List<String>, newTopics: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
    }
}
