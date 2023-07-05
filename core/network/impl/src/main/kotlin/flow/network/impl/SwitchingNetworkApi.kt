package flow.network.impl

import flow.network.api.NetworkApi
import flow.network.data.NetworkApiRepository
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto
import javax.inject.Inject

class SwitchingNetworkApi @Inject constructor(
    private val networkApiRepository: NetworkApiRepository,
) : NetworkApi {

    private suspend fun api(): NetworkApi = networkApiRepository.getApi()

    override suspend fun checkAuthorized(token: String) =
        api().checkAuthorized(token)

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ) = api().login(username, password, captchaSid, captchaCode, captchaValue)

    override suspend fun getFavorites(token: String) =
        api().getFavorites(token)

    override suspend fun addFavorite(token: String, id: String) =
        api().addFavorite(token, id)

    override suspend fun removeFavorite(token: String, id: String) =
        api().removeFavorite(token, id)

    override suspend fun getForum() =
        api().getForum()

    override suspend fun getCategory(id: String, page: Int?) =
        api().getCategory(id, page)

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
    ) = api().getSearchPage(
        token,
        searchQuery,
        categories,
        author,
        authorId,
        sortType,
        sortOrder,
        period,
        page,
    )

    override suspend fun getTopic(
        token: String,
        id: String,
        page: Int?,
    ) = api().getTopic(token, id, page)

    override suspend fun getTopicPage(
        token: String,
        id: String,
        page: Int?,
    ) = api().getTopicPage(token, id, page)

    override suspend fun getCommentsPage(
        token: String,
        id: String,
        page: Int?,
    ) = api().getCommentsPage(token, id, page)

    override suspend fun addComment(token: String, topicId: String, message: String) =
        api().addComment(token, topicId, message)

    override suspend fun getTorrent(token: String, id: String) =
        api().getTorrent(token, id)

    override suspend fun download(token: String, id: String) =
        api().download(token, id)
}
