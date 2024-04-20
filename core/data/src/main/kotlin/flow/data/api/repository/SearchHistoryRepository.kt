package flow.data.api.repository

import flow.models.search.Filter
import flow.models.search.Search
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun observeAll(): Flow<List<Search>>
    suspend fun add(filter: Filter)
    suspend fun remove(id: Int)
    suspend fun clear()
}
