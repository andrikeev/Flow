package me.rutrackersearch.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.rutrackersearch.data.auth.AuthObservable
import me.rutrackersearch.data.converters.parseList
import me.rutrackersearch.data.converters.parseTopic
import me.rutrackersearch.data.converters.readJson
import me.rutrackersearch.data.database.AppDatabase
import me.rutrackersearch.data.database.entity.FavoriteTopicEntity
import me.rutrackersearch.data.network.ServerApi
import me.rutrackersearch.domain.entity.Page
import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.topic.Topic
import me.rutrackersearch.domain.repository.FavoritesRepository
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val api: ServerApi,
    private val authObservable: AuthObservable,
    db: AppDatabase,
) : FavoritesRepository {

    private val dao = db.favoriteTopicDao()

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
        if (authObservable.authorised) {
            loadRemoteFavorites()
        }
    }

    override suspend fun add(topic: Topic) {
        withContext(Dispatchers.IO) {
            if (authObservable.authorised) {
                api.addFavorite(topic.id)
                loadRemoteFavorites()
            }
        }
        dao.insert(FavoriteTopicEntity.of(topic))
    }

    override suspend fun remove(topic: Topic) {
        if (authObservable.authorised) {
            api.removeFavorite(topic.id)
            loadRemoteFavorites()
        }
        dao.deleteById(topic.id)
    }

    override suspend fun update(topic: Topic) {
        dao.update(topic.id)
    }

    override suspend fun clear() {
        dao.deleteAll()
    }

    private suspend fun loadRemoteFavorites() {
        try {
            val firstPage = loadRemoteFavoritesPage(1)
            val topics = (firstPage.items + IntRange(2, firstPage.pages)
                .map { page -> loadRemoteFavoritesPage(page) }
                .flatMap { page -> page.items })
                .map { FavoriteTopicEntity.of(it) }
            dao.deleteAll()
            dao.insertAll(topics)
        } catch (e: Exception) {
            //Nothing
        }
    }

    private suspend fun loadRemoteFavoritesPage(page: Int): Page<Topic> {
        val json = api.favorites(page).readJson()
        return Page(
            items = json.getJSONArray("topics").parseList(JSONObject::parseTopic),
            page = json.getInt("page"),
            pages = json.getInt("pages"),
        )
    }
}
