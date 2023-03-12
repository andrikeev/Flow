package flow.testing.repository

import flow.data.api.repository.SuggestsRepository
import kotlinx.coroutines.flow.Flow

class TestSuggestsRepository : SuggestsRepository {
    override fun observeSuggests(): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun addSuggest(suggest: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
    }
}
