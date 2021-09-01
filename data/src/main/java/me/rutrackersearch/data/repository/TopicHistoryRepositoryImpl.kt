package me.rutrackersearch.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rutrackersearch.data.database.AppDatabase
import me.rutrackersearch.data.database.entity.HistoryTopicEntity
import me.rutrackersearch.domain.entity.topic.Topic
import me.rutrackersearch.domain.repository.TopicHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicHistoryRepositoryImpl @Inject constructor(
    db: AppDatabase,
) : TopicHistoryRepository {
    private val dao = db.historyTopicDao()

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
