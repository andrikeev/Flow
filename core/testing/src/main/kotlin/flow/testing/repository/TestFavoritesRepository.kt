package flow.testing.repository

import flow.data.api.FavoritesRepository
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import kotlinx.coroutines.flow.Flow

class TestFavoritesRepository : FavoritesRepository {
    override fun observeTopics(): Flow<List<TopicModel<out Topic>>> {
        TODO("Not yet implemented")
    }

    override fun observeIds(): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override fun observeUpdatedIds(): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun loadFavorites() {}

    override suspend fun add(topic: Topic) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(topic: Topic) {
        TODO("Not yet implemented")
    }

    override suspend fun update(topic: Topic) {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
    }
}
