package flow.data.impl

import flow.data.api.SearchHistoryRepository
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
    override fun observeSearchHistory(): Flow<List<Search>> {
        return searchHistoryDao.observerAll().map { entities ->
            entities.map(SearchHistoryEntity::toSearch)
        }
    }

    override suspend fun addSearch(filter: Filter) {
        searchHistoryDao.insert(SearchHistoryEntity.of(filter))
    }

    override suspend fun clear() {
        searchHistoryDao.deleteAll()
    }
}
