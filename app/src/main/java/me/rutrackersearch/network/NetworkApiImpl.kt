package me.rutrackersearch.network

import me.rutrackersearch.models.search.Order
import me.rutrackersearch.models.search.Period
import me.rutrackersearch.models.search.Sort
import me.rutrackersearch.network.parsers.parseComments
import me.rutrackersearch.network.parsers.parseFavoritesPage
import me.rutrackersearch.network.parsers.parseFormToken
import me.rutrackersearch.network.parsers.parseForumPage
import me.rutrackersearch.network.parsers.parseForumTree
import me.rutrackersearch.network.parsers.parseSearchPage
import me.rutrackersearch.network.parsers.parseTopic
import me.rutrackersearch.network.parsers.parseTorrent
import me.rutrackersearch.network.rutracker.RuTrackerApi
import javax.inject.Inject

class NetworkApiImpl @Inject constructor(
    private val ruTrackerApi: RuTrackerApi,
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

    override suspend fun forumTree() = parseForumTree(ruTrackerApi.getForumTree())

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
