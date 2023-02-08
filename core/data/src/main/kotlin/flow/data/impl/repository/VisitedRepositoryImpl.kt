package flow.data.impl.repository

import flow.data.api.repository.VisitedRepository
import flow.data.converters.toTopic
import flow.data.converters.toVisitedEntity
import flow.database.dao.VisitedTopicDao
import flow.database.entity.VisitedTopicEntity
import flow.models.topic.Topic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VisitedRepositoryImpl @Inject constructor(
    private val visitedTopicDao: VisitedTopicDao,
) : VisitedRepository {
    override fun observeTopics(): Flow<List<Topic>> {
        return visitedTopicDao.observerAll().map { entities ->
            entities.map(VisitedTopicEntity::toTopic)
        }
    }

    override fun observeIds(): Flow<List<String>> {
        return visitedTopicDao.observerAllIds()
    }

    override suspend fun add(topic: Topic) {
        visitedTopicDao.insert(topic.toVisitedEntity())
    }

    override suspend fun clear() {
        visitedTopicDao.deleteAll()
    }
}
