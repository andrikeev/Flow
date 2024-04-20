package flow.data.impl.repository

import flow.data.api.repository.SearchHistoryRepository
import flow.data.converters.toEntity
import flow.data.converters.toSearch
import flow.database.dao.SearchHistoryDao
import flow.database.entity.SearchHistoryEntity
import flow.models.search.Filter
import flow.models.search.Search
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao,
) : SearchHistoryRepository {
    override fun observeAll(): Flow<List<Search>> {
        return searchHistoryDao.observerAll().map { entities ->
            entities.map(SearchHistoryEntity::toSearch)
        }
    }

    override suspend fun add(filter: Filter) {
        searchHistoryDao.insert(filter.toEntity())
    }

    override suspend fun remove(id: Int) {
        searchHistoryDao.delete(id)
    }

    override suspend fun clear() {
        searchHistoryDao.deleteAll()
    }
}
