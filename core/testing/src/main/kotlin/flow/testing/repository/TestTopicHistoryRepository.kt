package flow.testing.repository

import flow.data.api.TopicHistoryRepository
import flow.models.topic.Topic
import kotlinx.coroutines.flow.Flow

class TestTopicHistoryRepository : TopicHistoryRepository {
    override fun observeTopics(): Flow<List<Topic>> {
        TODO("Not yet implemented")
    }

    override fun observeIds(): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun add(topic: Topic) {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
    }
}
