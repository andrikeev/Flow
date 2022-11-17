package flow.network.rutracker

import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.networkutils.getString
import flow.networkutils.post
import flow.networkutils.request
import flow.networkutils.url
import okhttp3.OkHttpClient
import javax.inject.Inject

internal class RuTrackerApiImpl @Inject constructor(
    private val httpClient: OkHttpClient,
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
        url(path, *queryParams)
        get()
    }.getString()

    private suspend fun post(
        path: String,
        queryParams: Array<out Pair<String, String>> = emptyArray(),
        formFields: Array<out Pair<String, String>> = emptyArray(),
    ) = httpClient.request {
        url(path, *queryParams)
        post { formFields.forEach { (k, v) -> addEncoded(k, v) } }
    }.getString()

    private val Sort.value: String
        get() = when (this) {
            Sort.DATE -> "1"
            Sort.TITLE -> "2"
            Sort.DOWNLOADED -> "4"
            Sort.SEEDS -> "10"
            Sort.LEECHES -> "11"
            Sort.SIZE -> "7"
        }

    private val Order.value: String
        get() = when (this) {
            Order.ASCENDING -> "1"
            Order.DESCENDING -> "2"
        }

    private val Period.value: String
        get() = when (this) {
            Period.ALL_TIME -> "-1"
            Period.TODAY -> "1"
            Period.LAST_THREE_DAYS -> "3"
            Period.LAST_WEEK -> "7"
            Period.LAST_TWO_WEEKS -> "14"
            Period.LAST_MONTH -> "32"
        }

    private fun pageToStart(page: Int?, pageSize: Int): String? = page?.let { (pageSize * (page - 1)).toString() }
}