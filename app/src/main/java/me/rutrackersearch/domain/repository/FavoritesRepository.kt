package me.rutrackersearch.domain.repository

import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Topic

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
