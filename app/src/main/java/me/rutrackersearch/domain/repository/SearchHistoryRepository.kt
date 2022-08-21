package me.rutrackersearch.domain.repository

import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.search.Search

interface SearchHistoryRepository {
    fun observeSearchHistory(): Flow<List<Search>>
    suspend fun addSearch(filter: Filter)
    suspend fun clear()
}
