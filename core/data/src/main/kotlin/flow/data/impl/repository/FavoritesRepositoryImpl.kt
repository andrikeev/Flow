package flow.data.impl.repository

import flow.data.api.repository.FavoritesRepository
import flow.data.converters.toFavoriteEntity
import flow.data.converters.toTopic
import flow.data.converters.toTopicModel
import flow.database.dao.FavoriteTopicDao
import flow.database.entity.FavoriteTopicEntity
import flow.logger.api.LoggerFactory
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteTopicDao: FavoriteTopicDao,
    loggerFactory: LoggerFactory,
) : FavoritesRepository {
    private val logger = loggerFactory.get("FavoritesRepositoryImpl")

    override fun observeTopics(): Flow<List<TopicModel<out Topic>>> {
        return favoriteTopicDao.observerAll()
            .onEach { logger.d { "observerAll: ids = ${it.map { it.id }}" } }
            .onEach { logger.d { "observerAll: timestamps = ${it.map { it.timestamp }}" } }
            .map { entities -> entities.map(FavoriteTopicEntity::toTopicModel) }
            .onEach { logger.d { "observeTopics: ids = ${it.map { it.topic.id }}" } }
    }

    override fun observeIds(): Flow<List<String>> {
        return favoriteTopicDao.observerAllIds()
            .onEach { logger.d { "observeIds: $it" } }
    }

    override fun observeUpdatedIds(): Flow<List<String>> {
        return favoriteTopicDao.observerUpdatedIds()
    }

    override suspend fun getIds(): List<String> {
        return favoriteTopicDao.getAllIds()
    }

    override suspend fun getTorrents(): List<Torrent> {
        return favoriteTopicDao.getAll()
            .map(FavoriteTopicEntity::toTopic)
            .filterIsInstance<Torrent>()
    }

    override suspend fun contains(id: String): Boolean {
        return favoriteTopicDao.getAllIds().contains(id)
    }

    override suspend fun add(topic: Topic) {
        favoriteTopicDao.insert(topic.toFavoriteEntity())
    }

    override suspend fun add(topics: List<Topic>) {
        val topicsToAdd = topics
            .map(Topic::toFavoriteEntity)
            .associateBy(FavoriteTopicEntity::id)
        val oldTopicIds = favoriteTopicDao.getAllIds().toSet()
        val newTopicEntities = topicsToAdd
            .filter { !oldTopicIds.contains(it.key) }
            .map(Map.Entry<String, FavoriteTopicEntity>::value)
        val updatedTopicEntities = topicsToAdd
            .filter { oldTopicIds.contains(it.key) }
        val updates = favoriteTopicDao.getAll()
            .mapNotNull { old ->
                updatedTopicEntities[old.id]?.let { update ->
                    val hasUpdate = old.magnetLink != null &&
                                    update.magnetLink != null &&
                                    old.magnetLink != update.magnetLink
                    old.copy(
                        title = update.title,
                        author = update.author ?: old.author,
                        category = update.category ?: old.category,
                        tags = update.tags ?: old.tags,
                        status = update.status ?: old.status,
                        date = update.date ?: old.date,
                        size = update.size ?: old.size,
                        seeds = update.seeds ?: old.seeds,
                        leeches = update.leeches ?: old.leeches,
                        magnetLink = update.magnetLink ?: old.magnetLink,
                        hasUpdate = hasUpdate,
                    )
                }
            }
        favoriteTopicDao.insert(newTopicEntities + updates)
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
        val existed = favoriteTopicDao.get(torrent.id)
        if (existed == null) {
            logger.d { "updateTorrent: insert" }
            favoriteTopicDao.insert(torrent.toFavoriteEntity())
        } else {
            logger.d { "updateTorrent: update" }
            favoriteTopicDao.insert(
                existed.copy(
                    title = torrent.title,
                    author = torrent.author,
                    category = torrent.category,
                    tags = torrent.tags,
                    status = torrent.status,
                    date = torrent.date,
                    size = torrent.size,
                    seeds = torrent.seeds,
                    leeches = torrent.leeches,
                    magnetLink = torrent.magnetLink,
                    hasUpdate = hasUpdate,
                )
            )
        }
    }

    override suspend fun update(topic: Topic) {
        favoriteTopicDao.update(topic.id)
    }

    override suspend fun clear() {
        favoriteTopicDao.deleteAll()
    }
}
