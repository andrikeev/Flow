package flow.network.impl

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.FileDto
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.Parameters
import java.net.URLEncoder

internal class RuTrackerInnerApiImpl(private val httpClient: HttpClient) : RuTrackerInnerApi {

    override suspend fun mainPage(token: String) = httpClient.get(Index) {
        header(CookieHeader, token)
    }.bodyAsText()

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): Pair<String?, String> = httpClient.submitForm(
        url = Login,
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
    ) = httpClient.get(Tracker) {
        header(CookieHeader, token)
        parameter("nm", searchQuery)
        parameter("f", categories)
        parameter("pn", author)
        parameter("pid", authorId)
        parameter("o", sortType?.value)
        parameter("s", sortOrder?.value)
        parameter("tm", period?.value)
        parameter("start", page?.let { (50 * (page - 1)).toString() })
    }.bodyAsText()

    override suspend fun forum() = httpClient.get(Index) {
        parameter("map", "0")
    }.bodyAsText()

    override suspend fun category(id: String, page: Int?) = httpClient.get(Forum) {
        parameter("f", id)
        parameter("start", page?.let { (50 * (page - 1)).toString() })
    }.bodyAsText()

    override suspend fun topic(
        token: String,
        id: String,
        page: Int?,
    ) = httpClient.get(Topic) {
        header(CookieHeader, token)
        parameter("t", id)
        parameter("start", page?.let { (30 * (page - 1)).toString() })
    }.bodyAsText()

    override suspend fun download(token: String, id: String) = httpClient.get(Download) {
        header(CookieHeader, token)
        parameter("t", id)
    }.let { response ->
        FileDto(
            contentDisposition = response.headers["Content-Disposition"].orEmpty(),
            contentType = response.headers["Content-Type"].orEmpty(),
            bytes = response.readBytes(),
        )
    }

    override suspend fun profile(userId: String) = httpClient.get(Profile) {
        parameter("mode", "viewprofile")
        parameter("u", userId)
    }.bodyAsText()

    override suspend fun postMessage(
        token: String,
        topicId: String,
        formToken: String,
        message: String,
    ) = httpClient.submitForm(Posting) {
        header(CookieHeader, token)
        formData {
            append("mode", "reply")
            append("submit_mode", "submit")
            append("t", topicId)
            append("form_token", formToken)
            append("message", message.toCp1251())
        }
    }.bodyAsText()

    override suspend fun favorites(token: String, page: Int?) = httpClient.get(Bookmarks) {
        header(CookieHeader, token)
        parameter("start", page?.let { (50 * (page - 1)).toString() })

    }.bodyAsText()

    override suspend fun addFavorite(
        token: String,
        id: String,
        formToken: String,
    ) = httpClient.submitForm(
        url = Bookmarks,
        block = { header(CookieHeader, token) },
        formParameters = Parameters.build {
            append("action", "bookmark_add")
            append("topic_id", id)
            append("form_token", formToken)
        },
    ).bodyAsText()

    override suspend fun removeFavorite(
        token: String,
        id: String,
        formToken: String,
    ) = httpClient.submitForm(
        url = Bookmarks,
        block = { header(CookieHeader, token) },
        formParameters = Parameters.build {
            append("action", "bookmark_delete")
            append("topic_id", id)
            append("form_token", formToken)
            append("request_origin", "from_topic_page")
        },
    ).bodyAsText()

    override suspend fun futureDownloads(token: String, page: Int?) = httpClient.get(Search) {
        header(CookieHeader, token)
        parameter("future_dls", null)
        parameter("start", page?.let { (50 * (page - 1)).toString() })
    }.bodyAsText()

    override suspend fun addFutureDownload(
        token: String,
        id: String,
        formToken: String,
    ) = httpClient.post(Ajax) {
        header(CookieHeader, token)
        parameter("action", "add_future_dl")
        parameter("topic_id", id)
        parameter("form_token", formToken)
    }.bodyAsText()

    override suspend fun removeFutureDownload(
        token: String,
        id: String,
        formToken: String,
    ) = httpClient.post(Ajax) {
        header(CookieHeader, token)
        parameter("action", "del_future_dl")
        parameter("topic_id", id)
        parameter("form_token", formToken)
    }.bodyAsText()

    private companion object {
        const val Login: String = "login.php"
        const val Profile: String = "profile.php"
        const val Index: String = "index.php"
        const val Tracker: String = "tracker.php"
        const val Forum: String = "viewforum.php"
        const val Topic: String = "viewtopic.php"
        const val Posting: String = "posting.php"
        const val Bookmarks: String = "bookmarks.php"
        const val Ajax: String = "ajax.php"
        const val Search: String = "search.php"
        const val Download: String = "dl.php"

        const val CookieHeader: String = "Cookie"
    }

    private fun String.toCp1251(): String = URLEncoder.encode(this, "Windows-1251")
}
