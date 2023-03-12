package flow.network.impl

import flow.network.api.NetworkApi
import flow.network.api.ProxyInnerApi
import flow.network.dto.ResultDto
import flow.network.dto.auth.AuthResponseDto
import flow.network.dto.error.FlowError
import flow.network.dto.forum.CategoryPageDto
import flow.network.dto.forum.ForumDto
import flow.network.dto.search.SearchPageDto
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto
import flow.network.dto.topic.CommentsPageDto
import flow.network.dto.topic.TopicDto
import flow.network.dto.topic.TorrentDto
import flow.network.dto.user.FavoritesDto
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal class ProxyNetworkApi(private val api: ProxyInnerApi) : NetworkApi {
    override suspend fun checkAuthorized(token: String) = ResultDto.Error(FlowError.Forbidden)

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): ResultDto<AuthResponseDto> =
        Json.decodeFromString(api.login(username, password, captchaSid, captchaCode, captchaValue))

    override suspend fun getFavorites(token: String): ResultDto<FavoritesDto> =
        Json.decodeFromString(api.favorites(token))

    override suspend fun addFavorite(token: String, id: String): ResultDto<Boolean> =
        Json.decodeFromString(api.addFavorite(token, id))

    override suspend fun removeFavorite(token: String, id: String): ResultDto<Boolean> =
        Json.decodeFromString(api.removeFavorite(token, id))

    override suspend fun getForum(): ResultDto<ForumDto> = Json.decodeFromString(api.forum())

    override suspend fun getCategory(id: String, page: Int?): ResultDto<CategoryPageDto> =
        Json.decodeFromString(api.category(id, page))

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
    ): ResultDto<SearchPageDto> = Json.decodeFromString(
        api.search(token, searchQuery, categories, author, authorId, sortType, sortOrder, period, page)
    )

    override suspend fun getTopic(token: String, id: String?, pid: String?, page: Int?): ResultDto<TopicDto> =
        Json.decodeFromString(api.topic(token, id, pid, page))

    override suspend fun getTorrent(token: String, id: String): ResultDto<TorrentDto> =
        Json.decodeFromString(api.torrent(token, id))

    override suspend fun getCommentsPage(
        token: String,
        id: String?,
        pid: String?,
        page: Int?,
    ): ResultDto<CommentsPageDto> = Json.decodeFromString(api.comments(token, id, pid, page))

    override suspend fun addComment(token: String, topicId: String, message: String): ResultDto<Boolean> =
        Json.decodeFromString(api.addComment(token, topicId, message))

    override suspend fun download(token: String, id: String) = ResultDto.Error(FlowError.Forbidden)
}
