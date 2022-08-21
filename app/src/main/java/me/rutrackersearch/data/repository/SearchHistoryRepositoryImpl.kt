package me.rutrackersearch.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rutrackersearch.data.database.AppDatabase
import me.rutrackersearch.data.database.entity.SearchHistoryEntity
import me.rutrackersearch.domain.repository.SearchHistoryRepository
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.search.Search
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    db: AppDatabase,
) : SearchHistoryRepository {
    private val dao = db.searchHistoryDao()

    override fun observeSearchHistory(): Flow<List<Search>> {
        return dao.observerAll().map { entities ->
            entities.map(SearchHistoryEntity::toSearch)
        }
    }

    override suspend fun addSearch(filter: Filter) {
        dao.insert(SearchHistoryEntity.of(filter))
    }

    override suspend fun clear() {
        dao.deleteAll()
    }
}
