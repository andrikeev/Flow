package flow.data.api

import flow.models.topic.Topic
import kotlinx.coroutines.flow.Flow

interface TopicHistoryRepository {
    fun observeTopics(): Flow<List<Topic>>
    fun observeIds(): Flow<List<String>>
    suspend fun add(topic: Topic)
    suspend fun clear()
}
