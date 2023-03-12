package flow.data.api.repository

import flow.models.topic.Topic
import kotlinx.coroutines.flow.Flow

interface VisitedRepository {
    fun observeTopics(): Flow<List<Topic>>
    fun observeIds(): Flow<List<String>>
    suspend fun add(topic: Topic)
    suspend fun clear()
}
