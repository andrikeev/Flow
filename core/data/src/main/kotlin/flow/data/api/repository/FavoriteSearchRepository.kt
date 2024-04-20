package flow.data.api.repository

import kotlinx.coroutines.flow.Flow

interface FavoriteSearchRepository {
    fun observeAll(): Flow<Set<Int>>
    suspend fun add(id: Int)
    suspend fun remove(id: Int)
    suspend fun clear()
}
