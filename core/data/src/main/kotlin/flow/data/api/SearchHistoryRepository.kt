package flow.data.api

import flow.models.search.Filter
import flow.models.search.Search
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun observeSearchHistory(): Flow<List<Search>>
    suspend fun addSearch(filter: Filter)
    suspend fun clear()
}
