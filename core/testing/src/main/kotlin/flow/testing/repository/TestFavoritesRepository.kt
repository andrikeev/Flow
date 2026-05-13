package flow.testing.repository

import flow.data.api.repository.FavoritesRepository
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestFavoritesRepository : FavoritesRepository {
    private val topicsFlow: MutableStateFlow<List<TopicModel<Topic>>> = MutableStateFlow(emptyList())

    override fun observeTopics(): Flow<List<TopicModel<out Topic>>> = topicsFlow

    override fun observeIds(): Flow<List<String>> =
        topicsFlow.map { topics -> topics.map { it.topic.id } }

    override fun observeUpdatedIds(): Flow<List<String>> =
        topicsFlow.map { topics -> topics.filter { it.hasUpdate }.map { it.topic.id } }

    override suspend fun getIds(): List<String> = topicsFlow.value.map { it.topic.id }

    override suspend fun getTorrents(): List<Torrent> =
        topicsFlow.value.mapNotNull { it.topic as? Torrent }

    override suspend fun contains(id: String): Boolean =
        topicsFlow.value.any { it.topic.id == id }

    override suspend fun add(topic: Topic) {
        topicsFlow.update { current ->
            if (current.any { it.topic.id == topic.id }) current
            else current + TopicModel<Topic>(topic = topic, isFavorite = true)
        }
    }

    override suspend fun add(topics: List<Topic>) {
        topics.forEach { add(it) }
    }

    override suspend fun remove(topic: Topic) {
        removeById(topic.id)
    }

    override suspend fun remove(topics: List<Topic>) {
        removeById(topics.map { it.id })
    }

    override suspend fun removeById(id: String) {
        topicsFlow.update { current -> current.filterNot { it.topic.id == id } }
    }

    override suspend fun removeById(ids: List<String>) {
        val idSet = ids.toSet()
        topicsFlow.update { current -> current.filterNot { it.topic.id in idSet } }
    }

    override suspend fun updateTorrent(torrent: Torrent, hasUpdate: Boolean) {
        topicsFlow.update { current ->
            current.map { model ->
                if (model.topic.id == torrent.id) {
                    TopicModel<Topic>(topic = torrent, isFavorite = true, hasUpdate = hasUpdate)
                } else {
                    model
                }
            }
        }
    }

    override suspend fun markVisited(id: String) {
        topicsFlow.update { current ->
            current.map { model ->
                if (model.topic.id == id) model.copy(isVisited = true, hasUpdate = false) else model
            }
        }
    }

    override suspend fun clear() {
        topicsFlow.update { emptyList() }
    }
}
