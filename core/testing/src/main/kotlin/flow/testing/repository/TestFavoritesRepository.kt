package flow.testing.repository

import flow.data.api.repository.FavoritesRepository
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
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

    override suspend fun getIds(): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getTorrents(): List<Torrent> {
        TODO("Not yet implemented")
    }

    override suspend fun contains(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun add(topic: Topic) {
        TODO("Not yet implemented")
    }

    override suspend fun add(topics: List<Topic>) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(topic: Topic) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(topics: List<Topic>) {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(ids: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTorrent(torrent: Torrent, hasUpdate: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun markVisited(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
    }
}
