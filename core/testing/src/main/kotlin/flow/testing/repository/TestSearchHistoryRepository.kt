package flow.testing.repository

import flow.data.api.SearchHistoryRepository
import flow.models.search.Filter
import flow.models.search.Search
import kotlinx.coroutines.flow.Flow

class TestSearchHistoryRepository : SearchHistoryRepository {
    override fun observeSearchHistory(): Flow<List<Search>> {
        TODO("Not yet implemented")
    }

    override suspend fun addSearch(filter: Filter) {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
    }
}
