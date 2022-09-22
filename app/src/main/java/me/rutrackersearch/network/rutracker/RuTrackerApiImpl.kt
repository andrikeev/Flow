package me.rutrackersearch.network.rutracker

import me.rutrackersearch.models.search.Order
import me.rutrackersearch.models.search.Order.ASCENDING
import me.rutrackersearch.models.search.Order.DESCENDING
import me.rutrackersearch.models.search.Period
import me.rutrackersearch.models.search.Period.ALL_TIME
import me.rutrackersearch.models.search.Period.LAST_MONTH
import me.rutrackersearch.models.search.Period.LAST_THREE_DAYS
import me.rutrackersearch.models.search.Period.LAST_TWO_WEEKS
import me.rutrackersearch.models.search.Period.LAST_WEEK
import me.rutrackersearch.models.search.Period.TODAY
import me.rutrackersearch.models.search.Sort
import me.rutrackersearch.models.search.Sort.DATE
import me.rutrackersearch.models.search.Sort.DOWNLOADED
import me.rutrackersearch.models.search.Sort.LEECHES
import me.rutrackersearch.models.search.Sort.SEEDS
import me.rutrackersearch.models.search.Sort.SIZE
import me.rutrackersearch.models.search.Sort.TITLE
import me.rutrackersearch.network.HostProvider
import me.rutrackersearch.network.utils.getString
import me.rutrackersearch.network.utils.post
import me.rutrackersearch.network.utils.request
import me.rutrackersearch.network.utils.url
import okhttp3.OkHttpClient
import javax.inject.Inject

class RuTrackerApiImpl @Inject constructor(
    private val httpClient: OkHttpClient,
    private val hostProvider: HostProvider,
) : RuTrackerApi {
    override suspend fun getMainPage() = get("index.php")

    override suspend fun search(
        query: String,
        sort: Sort,
        order: Order,
        period: Period,
        author: String,
        authorId: String,
        categories: String,
        page: Int
    ) = get(
        "tracker.php",
        "nm" to query,
        "f" to categories,
        "pn" to author,
        "pid" to authorId,
        "o" to sort.value,
        "s" to order.value,
        "tm" to period.value,
        "start" to pageToStart(page, 50),
    )

    override suspend fun getForum(
        id: String,
        page: Int,
    ) = get(
        "viewforum.php",
        "f" to id,
        "start" to pageToStart(page, 50),
    )

    override suspend fun getTopic(
        id: String?,
        pid: String?,
        page: Int?,
    ) = get(
        "viewtopic.php",
        if (!id.isNullOrEmpty()) {
            "t" to id
        } else {
            "p" to pid
        },
        "start" to pageToStart(page, 30),
    )

    override suspend fun getForumTree() = get("index.php", "map" to "0")

    override suspend fun postMessage(topicId: String, formToken: String, message: String) = post(
        path = "posting.php",
        formFields = arrayOf(
            "mode" to "reply",
            "submit_mode" to "submit",
            "t" to topicId,
            "form_token" to formToken,
            "message" to message,
        ),
    )

    override suspend fun getFavorites(page: Int?) = get(
        "bookmarks.php",
        "start" to page?.let { (50 * (page - 1)).toString() },
    )

    override suspend fun addFavorite(id: String, formToken: String) = post(
        "bookmarks.php",
        formFields = arrayOf(
            "action" to "bookmark_add",
            "topic_id" to id,
            "form_token" to formToken,
        ),
    )

    override suspend fun removeFavorite(id: String, formToken: String) = post(
        "bookmarks.php",
        formFields = arrayOf(
            "action" to "bookmark_delete",
            "topic_id" to id,
            "form_token" to formToken,
            "request_origin" to "from_topic_page",
        ),
    )

    private suspend fun get(path: String, vararg queryParams: Pair<String, String?>) = httpClient.request {
        url(hostProvider.host, path, *queryParams)
        get()
    }.getString()

    private suspend fun post(
        path: String,
        queryParams: Array<out Pair<String, String>> = emptyArray(),
        formFields: Array<out Pair<String, String>> = emptyArray(),
    ) = httpClient.request {
        url(hostProvider.host, path, *queryParams)
        post { formFields.forEach { (k, v) -> addEncoded(k, v) } }
    }.getString()

    private val Sort.value: String
        get() = when (this) {
            DATE -> "1"
            TITLE -> "2"
            DOWNLOADED -> "4"
            SEEDS -> "10"
            LEECHES -> "11"
            SIZE -> "7"
        }

    private val Order.value: String
        get() = when (this) {
            ASCENDING -> "1"
            DESCENDING -> "2"
        }

    private val Period.value: String
        get() = when (this) {
            ALL_TIME -> "-1"
            TODAY -> "1"
            LAST_THREE_DAYS -> "3"
            LAST_WEEK -> "7"
            LAST_TWO_WEEKS -> "14"
            LAST_MONTH -> "32"
        }

    private fun pageToStart(page: Int?, pageSize: Int): String? = page?.let { (pageSize * (page - 1)).toString() }
}
