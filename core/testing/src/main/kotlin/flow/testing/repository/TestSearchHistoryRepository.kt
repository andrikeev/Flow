package flow.testing.repository

import flow.data.api.repository.SearchHistoryRepository
import flow.models.search.Filter
import flow.models.search.Search
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestSearchHistoryRepository : SearchHistoryRepository {
    private val searchFlow: MutableStateFlow<List<Search>> = MutableStateFlow(emptyList())

    override fun observeSearchHistory(): Flow<List<Search>> = searchFlow

    override suspend fun addSearch(filter: Filter) {
        searchFlow.value = searchFlow.value.plus(Search(searchFlow.value.size, filter))
    }

    override suspend fun clear() {
        searchFlow.value = emptyList()
    }
}
