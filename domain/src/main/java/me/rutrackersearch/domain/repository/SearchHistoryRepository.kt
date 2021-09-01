package me.rutrackersearch.domain.repository

import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.domain.entity.search.Filter
import me.rutrackersearch.domain.entity.search.Search

interface SearchHistoryRepository {
    fun observeSearchHistory(): Flow<List<Search>>
    suspend fun addSearch(filter: Filter)
    suspend fun clear()
}
