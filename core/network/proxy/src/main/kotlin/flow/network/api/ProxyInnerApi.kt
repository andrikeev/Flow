package flow.network.api

import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto

internal interface ProxyInnerApi {
    suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): String
    suspend fun search(
        token: String?,
        searchQuery: String?,
        categories: String?,
        author: String?,
        authorId: String?,
        sortType: SearchSortTypeDto?,
        sortOrder: SearchSortOrderDto?,
        period: SearchPeriodDto?,
        page: Int?,
    ): String
    suspend fun forum(): String
    suspend fun category(id: String, page: Int?): String
    suspend fun topic(token: String?, id: String? = null, pid: String? = null, page: Int? = null): String
    suspend fun torrent(token: String?, id: String? = null): String
    suspend fun comments(token: String?, id: String? = null, pid: String? = null, page: Int? = null): String
    suspend fun profile(userId: String): String
    suspend fun addComment(token: String, topicId: String, message: String): String
    suspend fun favorites(token: String): String
    suspend fun addFavorite(token: String, id: String): String
    suspend fun removeFavorite(token: String, id: String): String
}
