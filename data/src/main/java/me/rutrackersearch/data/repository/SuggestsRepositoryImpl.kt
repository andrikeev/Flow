package me.rutrackersearch.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rutrackersearch.data.database.AppDatabase
import me.rutrackersearch.data.database.entity.SuggestEntity
import me.rutrackersearch.domain.repository.SuggestsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuggestsRepositoryImpl @Inject constructor(
    db: AppDatabase,
) : SuggestsRepository {
    private val dao = db.suggestDao()

    override fun observeSuggests(): Flow<List<String>> = dao.observerAll().map { suggests ->
        suggests.map(SuggestEntity::suggest)
    }

    override suspend fun addSuggest(suggest: String) {
        dao.insert(SuggestEntity.of(suggest))
    }

    override suspend fun clear() {
        dao.deleteAll()
    }
}
