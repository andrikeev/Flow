package flow.data.impl

import flow.data.api.TopicHistoryRepository
import flow.database.dao.HistoryTopicDao
import flow.database.entity.HistoryTopicEntity
import flow.models.topic.Topic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TopicHistoryRepositoryImpl @Inject constructor(
    private val historyTopicDao: HistoryTopicDao,
) : TopicHistoryRepository {
    override fun observeTopics(): Flow<List<Topic>> {
        return historyTopicDao.observerAll().map { entities ->
            entities.map(HistoryTopicEntity::toTopic)
        }
    }

    override fun observeIds(): Flow<List<String>> {
        return historyTopicDao.observerAllIds()
    }

    override suspend fun add(topic: Topic) {
        historyTopicDao.insert(HistoryTopicEntity.of(topic))
    }

    override suspend fun clear() {
        historyTopicDao.deleteAll()
    }
}
