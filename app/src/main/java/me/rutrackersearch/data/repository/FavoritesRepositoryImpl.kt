package me.rutrackersearch.data.repository

import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rutrackersearch.data.database.dao.FavoriteTopicDao
import me.rutrackersearch.data.database.entity.FavoriteTopicEntity
import me.rutrackersearch.data.workers.AddFavoriteWorker
import me.rutrackersearch.data.workers.LoadFavoritesWorker
import me.rutrackersearch.data.workers.RemoveFavoriteWorker
import me.rutrackersearch.data.workers.oneTimeWorkRequest
import me.rutrackersearch.domain.repository.FavoritesRepository
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.TopicModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val workManager: WorkManager,
    private val dao: FavoriteTopicDao,
) : FavoritesRepository {
    override fun observeTopics(): Flow<List<TopicModel<out Topic>>> {
        return dao.observerAll().map { entities ->
            entities.map(FavoriteTopicEntity::toTopicModel)
        }
    }

    override fun observeIds(): Flow<List<String>> {
        return dao.observerAllIds()
    }

    override fun observeUpdatedIds(): Flow<List<String>> {
        return dao.observerUpdatedIds()
    }

    override suspend fun loadFavorites() {
        val workRequest = oneTimeWorkRequest<LoadFavoritesWorker>()
        workManager.enqueue(workRequest)
    }

    override suspend fun add(topic: Topic) {
        dao.insert(FavoriteTopicEntity.of(topic))
        val data = AddFavoriteWorker.dataOf(topic)
        val workRequest = oneTimeWorkRequest<AddFavoriteWorker>(data)
        workManager.enqueue(workRequest)
    }

    override suspend fun remove(topic: Topic) {
        dao.deleteById(topic.id)
        val data = RemoveFavoriteWorker.dataOf(topic)
        val workRequest = oneTimeWorkRequest<RemoveFavoriteWorker>(data)
        workManager.enqueue(workRequest)
    }

    override suspend fun update(topic: Topic) {
        dao.update(topic.id)
    }

    override suspend fun clear() {
        dao.deleteAll()
    }
}
