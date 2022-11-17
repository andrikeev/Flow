package flow.data.impl

import flow.data.api.FavoritesRepository
import flow.database.dao.FavoriteTopicDao
import flow.database.entity.FavoriteTopicEntity
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import flow.work.api.BackgroundService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteTopicDao: FavoriteTopicDao,
    private val backgroundService: BackgroundService,
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

    override suspend fun loadFavorites() {
        backgroundService.loadFavorites()
    }

    override suspend fun add(topic: Topic) {
        favoriteTopicDao.insert(FavoriteTopicEntity.of(topic))
        backgroundService.addFavoriteTopic(topic.id, topic is Torrent)
    }

    override suspend fun remove(topic: Topic) {
        favoriteTopicDao.deleteById(topic.id)
        backgroundService.removeFavoriteTopic(topic.id)
    }

    override suspend fun update(topic: Topic) {
        favoriteTopicDao.update(topic.id)
    }

    override suspend fun clear() {
        favoriteTopicDao.deleteAll()
    }
}
