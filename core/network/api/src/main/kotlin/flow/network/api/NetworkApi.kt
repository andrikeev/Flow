package flow.network.api

import flow.network.dto.FileDto
import flow.network.dto.ResultDto
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

interface NetworkApi {
    suspend fun checkAuthorized(token: String): ResultDto<Boolean>
    suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): ResultDto<AuthResponseDto>
    suspend fun getFavorites(token: String): ResultDto<FavoritesDto>
    suspend fun addFavorite(token: String, id: String): ResultDto<Boolean>
    suspend fun removeFavorite(token: String, id: String): ResultDto<Boolean>
    suspend fun getForum(): ResultDto<ForumDto>
    suspend fun getCategory(id: String, page: Int?): ResultDto<CategoryPageDto>
    suspend fun getSearchPage(
        token: String,
        searchQuery: String?,
        categories: String?,
        author: String?,
        authorId: String?,
        sortType: SearchSortTypeDto?,
        sortOrder: SearchSortOrderDto?,
        period: SearchPeriodDto?,
        page: Int?,
    ): ResultDto<SearchPageDto>
    suspend fun getTopic(token: String, id: String?, pid: String?, page: Int?): ResultDto<ForumTopicDto>
    suspend fun getCommentsPage(token: String, id: String?, pid: String?, page: Int?): ResultDto<CommentsPageDto>
    suspend fun addComment(token: String, topicId: String, message: String): ResultDto<Boolean>
    suspend fun getTorrent(token: String, id: String): ResultDto<TorrentDto>
    suspend fun download(token: String, id: String): ResultDto<FileDto>
}
