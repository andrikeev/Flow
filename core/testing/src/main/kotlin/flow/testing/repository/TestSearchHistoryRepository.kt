package flow.testing.repository

import flow.data.api.repository.SearchHistoryRepository
import flow.models.search.Filter
import flow.models.search.Search
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class TestSearchHistoryRepository : SearchHistoryRepository {
    private val searchFlow: MutableStateFlow<List<Search>> = MutableStateFlow(emptyList())

    override fun observeAll(): Flow<List<Search>> = searchFlow

    override suspend fun add(filter: Filter) {
        searchFlow.update { it.plus(Search(it.size, filter)) }
    }

    override suspend fun remove(id: Int) {
        searchFlow.update { it.filter { it.id == id } }
    }

    override suspend fun clear() {
        searchFlow.update { emptyList() }
    }
}
