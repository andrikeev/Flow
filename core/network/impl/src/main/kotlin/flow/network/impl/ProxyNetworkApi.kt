package flow.network.impl

import flow.network.api.NetworkApi
import flow.network.dto.auth.AuthResponseDto
import flow.network.dto.forum.CategoryPageDto
import flow.network.dto.forum.ForumDto
import flow.network.dto.search.SearchPageDto
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto
import flow.network.dto.topic.CommentsPageDto
import flow.network.dto.topic.ForumTopicDto
import flow.network.dto.topic.TorrentDto
import flow.network.dto.user.FavoritesDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters

internal class ProxyNetworkApi(private val httpClient: HttpClient) : NetworkApi {
    override suspend fun checkAuthorized(token: String) = error("Not implemented")

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): AuthResponseDto = httpClient.submitForm(
        url = "/login",
        formParameters = Parameters.build {
            append("username", username)
            append("password", password)
            append("cap_sid", captchaSid.orEmpty())
            append("cap_code", captchaCode.orEmpty())
            append("cap_val", captchaValue.orEmpty())
        },
    ).body()

    override suspend fun getFavorites(
        token: String,
    ): FavoritesDto = httpClient.get("/favorites") {
        token(token)
    }.body()

    override suspend fun addFavorite(
        token: String,
        id: String,
    ): Boolean = httpClient.post("/favorites/add/$id") {
        token(token)
    }.body()

    override suspend fun removeFavorite(
        token: String,
        id: String,
    ): Boolean = httpClient.post("/favorites/remove/$id") {
        token(token)
    }.body()

    override suspend fun getForum(): ForumDto = httpClient.get("/forum").body()

    override suspend fun getCategory(
        id: String,
        page: Int?,
    ): CategoryPageDto = httpClient.get("/forum/$id") {
        parameter("page", page)
    }.body()

    override suspend fun getSearchPage(
        token: String,
        searchQuery: String?,
        categories: String?,
        author: String?,
        authorId: String?,
        sortType: SearchSortTypeDto?,
        sortOrder: SearchSortOrderDto?,
        period: SearchPeriodDto?,
        page: Int?,
    ): SearchPageDto = httpClient.get("/search") {
        token(token)
        parameter("query", searchQuery)
        parameter("categories", categories)
        parameter("author", author)
        parameter("authorId", authorId)
        parameter("sort", sortType)
        parameter("order", sortOrder)
        parameter("period", period)
        parameter("page", page)
    }.body()

    override suspend fun getTopic(
        token: String,
        id: String,
        page: Int?,
    ): ForumTopicDto = httpClient.get("/topic/$id") {
        token(token)
        parameter("page", page)
    }.body()

    override suspend fun getTorrent(
        token: String,
        id: String,
    ): TorrentDto = httpClient.get("/torrent/$id") {
        token(token)
    }.body()

    override suspend fun getCommentsPage(
        token: String,
        id: String,
        page: Int?,
    ): CommentsPageDto = httpClient.get("/comments/$id") {
        token(token)
        parameter("page", page)
    }.body()

    override suspend fun addComment(
        token: String,
        topicId: String,
        message: String,
    ): Boolean = httpClient.submitForm("/comments/$topicId/add") {
        token(token)
        setBody(message)
    }.body()

    override suspend fun download(token: String, id: String) = error("Not implemented")

    companion object {
        fun HttpRequestBuilder.token(token: String?) {
            if (token != null) {
                header("Auth-Token", token)
            }
        }
    }
}
