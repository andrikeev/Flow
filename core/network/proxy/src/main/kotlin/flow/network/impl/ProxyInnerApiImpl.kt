package flow.network.impl

import flow.network.api.ProxyInnerApi
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

internal class ProxyInnerApiImpl(private val httpClient: HttpClient) : ProxyInnerApi {

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ) = httpClient.get("/login") {
        parameter("username", username)
        parameter("password", password)
        parameter("cap_sid", captchaSid)
        parameter("cap_code", captchaCode)
        parameter("cap_val", captchaValue)
    }.bodyAsText()

    override suspend fun search(
        token: String?,
        searchQuery: String?,
        categories: String?,
        author: String?,
        authorId: String?,
        sortType: SearchSortTypeDto?,
        sortOrder: SearchSortOrderDto?,
        period: SearchPeriodDto?,
        page: Int?
    ) = httpClient.get("/search") {
        token(token)
        parameter("query", searchQuery)
        parameter("categories", categories)
        parameter("author", author)
        parameter("authorId", authorId)
        parameter("sort", sortType)
        parameter("order", sortOrder)
        parameter("period", period)
        parameter("page", page)
    }.bodyAsText()

    override suspend fun forum() = httpClient.get("/forum").bodyAsText()

    override suspend fun category(id: String, page: Int?) = httpClient.get("/category") {
        parameter("id", id)
        parameter("page", page)
    }.bodyAsText()

    override suspend fun topic(token: String?, id: String?, pid: String?, page: Int?) = httpClient.get("/topic") {
        token(token)
        parameter("id", id)
        parameter("pid", pid)
        parameter("page", page)
    }.bodyAsText()

    override suspend fun torrent(token: String?, id: String?) = httpClient.get("/torrent") {
        token(token)
        parameter("id", id)
    }.bodyAsText()

    override suspend fun comments(token: String?, id: String?, pid: String?, page: Int?) = httpClient.get("/comments") {
        token(token)
        parameter("id", id)
        parameter("pid", pid)
        parameter("page", page)
    }.bodyAsText()

    override suspend fun profile(userId: String) = httpClient.get("/topic") { parameter("userId", userId) }.bodyAsText()

    override suspend fun addComment(
        token: String,
        topicId: String,
        message: String,
    ) = httpClient.post("/comments/add") {
        token(token)
        parameter("topicId", topicId)
        parameter("message", message)
    }.bodyAsText()

    override suspend fun favorites(token: String) = httpClient.get("/favorites") { token(token) }.bodyAsText()

    override suspend fun addFavorite(
        token: String,
        id: String
    ) = httpClient.post("/favorites/add") {
        token(token)
        parameter("id", id)
    }.bodyAsText()

    override suspend fun removeFavorite(
        token: String,
        id: String,
    ) = httpClient.post("/favorites/remove") {
        token(token)
        parameter("id", id)
    }.bodyAsText()

    companion object {
        fun HttpRequestBuilder.token(token: String?) {
            if (token != null) {
                header("Auth-Token", token)
            }
        }
    }
}
