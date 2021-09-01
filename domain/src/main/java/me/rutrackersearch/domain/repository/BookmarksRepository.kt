package me.rutrackersearch.domain.repository

import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.domain.entity.CategoryModel
import me.rutrackersearch.domain.entity.forum.Category

interface BookmarksRepository {
    fun observeBookmarks(): Flow<List<CategoryModel>>
    fun observeIds(): Flow<List<String>>
    fun observeNewTopics(): Flow<List<String>>
    fun observeNewTopics(id: String): Flow<List<String>>
    suspend fun add(category: Category)
    suspend fun remove(category: Category)
    suspend fun clear()
    suspend fun markVisited(id: String)
}
