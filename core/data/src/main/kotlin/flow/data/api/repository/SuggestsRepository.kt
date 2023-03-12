package flow.data.api.repository

import kotlinx.coroutines.flow.Flow

interface SuggestsRepository {
    fun observeSuggests(): Flow<List<String>>
    suspend fun addSuggest(suggest: String)
    suspend fun clear()
}
