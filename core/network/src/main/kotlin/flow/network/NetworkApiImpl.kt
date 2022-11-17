package flow.network

import flow.dispatchers.api.Dispatchers
import flow.models.forum.ForumTree
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.network.parsers.parseComments
import flow.network.parsers.parseFavoritesPage
import flow.network.parsers.parseFormToken
import flow.network.parsers.parseForumPage
import flow.network.parsers.parseForumTree
import flow.network.parsers.parseSearchPage
import flow.network.parsers.parseTopic
import flow.network.parsers.parseTorrent
import flow.network.rutracker.RuTrackerApi
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class NetworkApiImpl @Inject constructor(
    private val ruTrackerApi: RuTrackerApi,
    private val dispatchers: Dispatchers,
) : NetworkApi {
    override suspend fun search(
        query: String,
        sort: Sort,
        order: Order,
        period: Period,
        author: String,
        authorId: String,
        categories: String,
        page: Int
    ) = parseSearchPage(
        ruTrackerApi.search(
            query = query,
            sort = sort,
            order = order,
            period = period,
            author = author,
            authorId = authorId,
            categories = categories,
            page = page,
        )
    )

    override suspend fun forumTree(): ForumTree {
        val response = withContext(dispatchers.io) {
            ruTrackerApi.getForumTree()
        }
        return withContext(dispatchers.default) {
            parseForumTree(response)
        }
    }

    override suspend fun category(id: String, page: Int) = parseForumPage(ruTrackerApi.getForum(id, page))

    override suspend fun favorites(page: Int) = parseFavoritesPage(ruTrackerApi.getFavorites(page))

    override suspend fun addFavorite(id: String) {
        val response = ruTrackerApi.addFavorite(id, getFormToken())
        if (!response.contains("Тема добавлена")) {
            throw RuntimeException("Error adding favorite")
        }
    }

    override suspend fun removeFavorite(id: String) {
        val response = ruTrackerApi.removeFavorite(id, getFormToken())
        if (!response.contains("Тема удалена")) {
            throw RuntimeException("Error removing favorite")
        }
    }

    override suspend fun topic(id: String, pid: String) = parseTopic(ruTrackerApi.getTopic(id = id, pid = pid))

    override suspend fun comments(id: String, page: Int) = parseComments(ruTrackerApi.getTopic(id = id, page = page))

    override suspend fun addComment(topicId: String, message: String) {
        ruTrackerApi.postMessage(topicId, getFormToken(), message)
    }

    override suspend fun torrent(id: String) = parseTorrent(ruTrackerApi.getTopic(id = id))

    private suspend fun getFormToken() = parseFormToken(ruTrackerApi.getMainPage())
}
