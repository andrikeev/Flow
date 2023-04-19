package flow.data.api.repository

import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeTopics(): Flow<List<TopicModel<out Topic>>>
    fun observeIds(): Flow<List<String>>
    fun observeUpdatedIds(): Flow<List<String>>
    suspend fun getIds(): List<String>
    suspend fun getTorrents(): List<Torrent>
    suspend fun contains(id: String): Boolean
    suspend fun add(topic: Topic)
    suspend fun add(topics: List<Topic>)
    suspend fun remove(topic: Topic)
    suspend fun remove(topics: List<Topic>)
    suspend fun removeById(id: String)
    suspend fun removeById(ids: List<String>)
    suspend fun updateTorrent(torrent: Torrent, hasUpdate: Boolean)
    suspend fun update(topic: Topic)
    suspend fun clear()
}
