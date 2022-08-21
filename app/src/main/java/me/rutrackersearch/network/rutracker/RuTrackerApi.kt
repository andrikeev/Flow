package me.rutrackersearch.network.rutracker

import me.rutrackersearch.models.search.Order
import me.rutrackersearch.models.search.Period
import me.rutrackersearch.models.search.Sort

interface RuTrackerApi {

    suspend fun getMainPage(): String

    suspend fun search(
        query: String,
        sort: Sort,
        order: Order,
        period: Period,
        author: String,
        authorId: String,
        categories: String,
        page: Int
    ): String

    suspend fun getForum(
        id: String,
        page: Int,
    ): String

    suspend fun getTopic(
        id: String? = null,
        pid: String? = null,
        page: Int? = null,
    ): String

    suspend fun getForumTree(): String

    suspend fun postMessage(
        topicId: String,
        formToken: String,
        message: String
    ): String

    suspend fun getFavorites(page: Int?): String

    suspend fun addFavorite(
        id: String,
        formToken: String
    ): String

    suspend fun removeFavorite(
        id: String,
        formToken: String
    ): String
}
