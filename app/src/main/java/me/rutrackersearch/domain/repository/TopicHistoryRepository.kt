package me.rutrackersearch.domain.repository

import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.models.topic.Topic

interface TopicHistoryRepository {
    fun observeTopics(): Flow<List<Topic>>
    fun observeIds(): Flow<List<String>>
    suspend fun add(topic: Topic)
    suspend fun clear()
}
