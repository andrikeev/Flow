package flow.network.impl

import flow.network.api.NetworkApi
import flow.network.domain.AddCommentUseCase
import flow.network.domain.AddFavoriteUseCase
import flow.network.domain.CheckAuthorisedUseCase
import flow.network.domain.GetCategoryPageUseCase
import flow.network.domain.GetCommentsPageUseCase
import flow.network.domain.GetFavoritesUseCase
import flow.network.domain.GetForumUseCase
import flow.network.domain.GetSearchPageUseCase
import flow.network.domain.GetTopicUseCase
import flow.network.domain.GetTorrentFileUseCase
import flow.network.domain.GetTorrentUseCase
import flow.network.domain.LoginUseCase
import flow.network.domain.RemoveFavoriteUseCase
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto

internal class RuTrackerNetworkApi(
    private val addCommentUseCase: AddCommentUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val checkAuthorisedUseCase: CheckAuthorisedUseCase,
    private val getCategoryPageUseCase: GetCategoryPageUseCase,
    private val getCommentsPageUseCase: GetCommentsPageUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val getForumUseCase: GetForumUseCase,
    private val getSearchPageUseCase: GetSearchPageUseCase,
    private val getTopicUseCase: GetTopicUseCase,
    private val getTorrentFileUseCase: GetTorrentFileUseCase,
    private val getTorrentUseCase: GetTorrentUseCase,
    private val loginUseCase: LoginUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
) : NetworkApi {
    override suspend fun checkAuthorized(token: String) = checkAuthorisedUseCase.invoke(token)

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ) = loginUseCase.invoke(username, password, captchaSid, captchaCode, captchaValue)

    override suspend fun getFavorites(token: String) = getFavoritesUseCase.invoke(token)

    override suspend fun addFavorite(token: String, id: String) = addFavoriteUseCase.invoke(token, id)

    override suspend fun removeFavorite(token: String, id: String) = removeFavoriteUseCase.invoke(token, id)

    override suspend fun getForum() = getForumUseCase.invoke()

    override suspend fun getCategory(id: String, page: Int?) = getCategoryPageUseCase.invoke(id, page)

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
    ) = getSearchPageUseCase.invoke(token, searchQuery, categories, author, authorId, sortType, sortOrder, period, page)

    override suspend fun getTopic(
        token: String,
        id: String,
        page: Int?,
    ) = getTopicUseCase.invoke(token, id, page)

    override suspend fun getCommentsPage(
        token: String,
        id: String,
        page: Int?,
    ) = getCommentsPageUseCase.invoke(token, id, page)

    override suspend fun addComment(
        token: String,
        topicId: String,
        message: String,
    ) = addCommentUseCase.invoke(token, topicId, message)

    override suspend fun getTorrent(token: String, id: String) = getTorrentUseCase.invoke(token, id)

    override suspend fun download(token: String, id: String) = getTorrentFileUseCase.invoke(token, id)
}