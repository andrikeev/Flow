package flow.network.api

import flow.network.dto.FileDto
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto

internal interface RuTrackerInnerApi {
    suspend fun mainPage(token: String): String
    suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): Pair<String?, String>
    suspend fun search(
        token: String,
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
    suspend fun topic(token: String, id: String? = null, pid: String? = null, page: Int? = null): String
    suspend fun download(token: String, id: String): FileDto
    suspend fun profile(userId: String): String
    suspend fun postMessage(token: String, topicId: String, formToken: String, message: String): String
    suspend fun favorites(token: String, page: Int?): String
    suspend fun addFavorite(token: String, id: String, formToken: String): String
    suspend fun removeFavorite(token: String, id: String, formToken: String): String
    suspend fun futureDownloads(token: String, page: Int?): String
    suspend fun addFutureDownload(token: String, id: String, formToken: String): String
    suspend fun removeFutureDownload(token: String, id: String, formToken: String): String
}
