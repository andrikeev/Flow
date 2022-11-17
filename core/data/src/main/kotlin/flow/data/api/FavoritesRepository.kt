package flow.data.api

import flow.models.topic.Topic
import flow.models.topic.TopicModel
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeTopics(): Flow<List<TopicModel<out Topic>>>
    fun observeIds(): Flow<List<String>>
    fun observeUpdatedIds(): Flow<List<String>>
    suspend fun loadFavorites()
    suspend fun add(topic: Topic)
    suspend fun remove(topic: Topic)
    suspend fun update(topic: Topic)
    suspend fun clear()
}
