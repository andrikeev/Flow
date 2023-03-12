package flow.data.impl.repository

import flow.data.api.repository.FavoritesRepository
import flow.data.converters.toFavoriteEntity
import flow.data.converters.toTopic
import flow.data.converters.toTopicModel
import flow.database.dao.FavoriteTopicDao
import flow.database.entity.FavoriteTopicEntity
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteTopicDao: FavoriteTopicDao,
) : FavoritesRepository {
    override fun observeTopics(): Flow<List<TopicModel<out Topic>>> {
        return favoriteTopicDao.observerAll().map { entities ->
            entities.map(FavoriteTopicEntity::toTopicModel)
        }
    }

    override fun observeIds(): Flow<List<String>> {
        return favoriteTopicDao.observerAllIds()
    }

    override fun observeUpdatedIds(): Flow<List<String>> {
        return favoriteTopicDao.observerUpdatedIds()
    }

    override suspend fun getIds(): List<String> {
        return favoriteTopicDao.getAllIds()
    }

    override suspend fun getTorrents(): List<Torrent> {
        return favoriteTopicDao.getAll().map(FavoriteTopicEntity::toTopic).filterIsInstance<Torrent>()
    }

    override suspend fun add(topic: Topic) {
        favoriteTopicDao.insert(topic.toFavoriteEntity())
    }

    override suspend fun add(topics: List<Topic>) {
        favoriteTopicDao.insert(topics.map(Topic::toFavoriteEntity))
    }

    override suspend fun remove(topic: Topic) {
        favoriteTopicDao.delete(topic.id)
    }

    override suspend fun remove(topics: List<Topic>) {
        favoriteTopicDao.delete(topics.map(Topic::id))
    }

    override suspend fun removeById(id: String) {
        favoriteTopicDao.delete(id)
    }

    override suspend fun removeById(ids: List<String>) {
        favoriteTopicDao.delete(ids)
    }

    override suspend fun updateTorrent(torrent: Torrent, hasUpdate: Boolean) {
        favoriteTopicDao.insert(torrent.toFavoriteEntity().copy(hasUpdate = hasUpdate))
    }

    override suspend fun update(topic: Topic) {
        favoriteTopicDao.update(topic.id)
    }

    override suspend fun clear() {
        favoriteTopicDao.deleteAll()
    }
}
