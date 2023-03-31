package flow.data.api.repository

import flow.models.forum.Category
import flow.models.forum.CategoryModel
import kotlinx.coroutines.flow.Flow

interface BookmarksRepository {
    fun observeBookmarks(): Flow<List<CategoryModel>>
    fun observeIds(): Flow<List<String>>
    fun observeNewTopics(): Flow<List<String>>
    fun observeNewTopics(id: String): Flow<List<String>>
    suspend fun getAllBookmarks(): List<Category>
    suspend fun getTopics(id: String): List<String>
    suspend fun getNewTopics(id: String): List<String>
    suspend fun isBookmark(id: String): Boolean
    suspend fun add(category: Category)
    suspend fun remove(id: String)
    suspend fun update(id: String, topics: List<String>, newTopics: List<String>)
    suspend fun clear()
}
