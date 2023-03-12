package flow.network.impl

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.FileDto
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.net.URLEncoder

internal class RuTrackerInnerApiImpl(private val httpClient: HttpClient) : RuTrackerInnerApi {

    override suspend fun mainPage(token: String) = httpClient.get(INDEX) {
        header(COOKIE_HEADER, token)
    }.bodyAsText()

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): Pair<String?, String> = httpClient.submitForm(
        url = LOGIN,
        formParameters = Parameters.build {
            append("login_username", username.toCp1251())
            append("login_password", password.toCp1251())
            append("login", "Вход")
            if (captchaSid != null && captchaCode != null && captchaValue != null) {
                append("cap_sid", captchaSid)
                append(captchaCode, captchaValue)
            }
        },
    ).let { response ->
        val cookie = response.headers.getAll("Set-Cookie").orEmpty()
        val token = cookie.firstOrNull { !it.contains("bb_ssl") }
        token to response.bodyAsText()
    }

    override suspend fun search(
        token: String,
        searchQuery: String?,
        categories: String?,
        author: String?,
        authorId: String?,
        sortType: SearchSortTypeDto?,
        sortOrder: SearchSortOrderDto?,
        period: SearchPeriodDto?,
        page: Int?,
    ) = httpClient.get(TRACKER) {
        header(COOKIE_HEADER, token)
        parameter("nm", searchQuery)
        parameter("f", categories)
        parameter("pn", author)
        parameter("pid", authorId)
        parameter("o", sortType?.value)
        parameter("s", sortOrder?.value)
        parameter("tm", period?.value)
        parameter("start", page?.let { (50 * (page - 1)).toString() })
    }.bodyAsText()

    override suspend fun forum() = httpClient.get(INDEX) {
        parameter("map", "0")
    }.bodyAsText()

    override suspend fun category(id: String, page: Int?) = httpClient.get(FORUM) {
        parameter("f", id)
        parameter("start", page?.let { (50 * (page - 1)).toString() })
    }.bodyAsText()

    override suspend fun topic(
        token: String,
        id: String?,
        pid: String?,
        page: Int?,
    ) = httpClient.get(TOPIC) {
        header(COOKIE_HEADER, token)
        if (!id.isNullOrEmpty()) {
            parameter("t", id)
        } else {
            parameter("p", pid)
        }
        parameter("start", page?.let { (30 * (page - 1)).toString() })
    }.bodyAsText()

    override suspend fun download(token: String, id: String) = httpClient.get(DOWNLOAD) {
        header(COOKIE_HEADER, token)
        parameter("t", id)
    }.let { response ->
        FileDto(
            contentDisposition = response.headers["Content-Disposition"].orEmpty(),
            contentType = response.headers["Content-Type"].orEmpty(),
            bytes = response.readBytes(),
        )
    }

    override suspend fun profile(userId: String) = httpClient.get(PROFILE) {
        parameter("mode", "viewprofile")
        parameter("u", userId)
    }.bodyAsText()

    override suspend fun postMessage(
        token: String,
        topicId: String,
        formToken: String,
        message: String,
    ) = httpClient.post(POSTING) {
        header(COOKIE_HEADER, token)
        formData {
            append("mode", "reply")
            append("submit_mode", "submit")
            append("t", topicId)
            append("form_token", formToken)
            append("message", message.toCp1251())
        }
    }.bodyAsText()

    override suspend fun favorites(token: String, page: Int?) = httpClient.get(BOOKMARKS) {
        header(COOKIE_HEADER, token)
        parameter("start", page?.let { (50 * (page - 1)).toString() })

    }.bodyAsText()

    override suspend fun addFavorite(
        token: String,
        id: String,
        formToken: String,
    ) = httpClient.post(BOOKMARKS) {
        header(COOKIE_HEADER, token)
        formData {
            append("action", "bookmark_add")
            append("topic_id", id)
            append("form_token", formToken)
        }
    }.bodyAsText()

    override suspend fun removeFavorite(
        token: String,
        id: String,
        formToken: String,
    ) = httpClient.post(BOOKMARKS) {
        header(COOKIE_HEADER, token)
        formData {
            append("action", "bookmark_delete")
            append("topic_id", id)
            append("form_token", formToken)
            append("request_origin", "from_topic_page")
        }
    }.bodyAsText()

    override suspend fun futureDownloads(token: String, page: Int?) = httpClient.get(SEARCH) {
        header(COOKIE_HEADER, token)
        parameter("future_dls", null)
        parameter("start", page?.let { (50 * (page - 1)).toString() })
    }.bodyAsText()

    override suspend fun addFutureDownload(
        token: String,
        id: String,
        formToken: String,
    ) = httpClient.post(AJAX) {
        header(COOKIE_HEADER, token)
        parameter("action", "add_future_dl")
        parameter("topic_id", id)
        parameter("form_token", formToken)
    }.bodyAsText()

    override suspend fun removeFutureDownload(
        token: String,
        id: String,
        formToken: String,
    ) = httpClient.post(AJAX) {
        header(COOKIE_HEADER, token)
        parameter("action", "del_future_dl")
        parameter("topic_id", id)
        parameter("form_token", formToken)
    }.bodyAsText()

    private companion object {

        const val LOGIN: String = "login.php"
        const val PROFILE: String = "profile.php"
        const val INDEX: String = "index.php"
        const val TRACKER: String = "tracker.php"
        const val FORUM: String = "viewforum.php"
        const val TOPIC: String = "viewtopic.php"
        const val POSTING: String = "posting.php"
        const val BOOKMARKS: String = "bookmarks.php"
        const val AJAX: String = "ajax.php"
        const val SEARCH: String = "search.php"
        const val DOWNLOAD: String = "dl.php"

        const val COOKIE_HEADER: String = "Cookie"
    }

    private fun String.toCp1251(): String = URLEncoder.encode(this, "Windows-1251")
}
