package me.rutrackersearch.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rutrackersearch.data.database.dao.HistoryTopicDao
import me.rutrackersearch.data.database.entity.HistoryTopicEntity
import me.rutrackersearch.domain.repository.TopicHistoryRepository
import me.rutrackersearch.models.topic.Topic
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicHistoryRepositoryImpl @Inject constructor(
    private val dao: HistoryTopicDao,
) : TopicHistoryRepository {
    override fun observeTopics(): Flow<List<Topic>> {
        return dao.observerAll().map { entities ->
            entities.map(HistoryTopicEntity::toTopic)
        }
    }

    override fun observeIds(): Flow<List<String>> {
        return dao.observerAllIds()
    }

    override suspend fun add(topic: Topic) {
        dao.insert(HistoryTopicEntity.of(topic))
    }

    override suspend fun clear() {
        dao.deleteAll()
    }
}
