package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.search.SearchPageDto
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto

internal class GetSearchPageUseCase(
    private val api: RuTrackerInnerApi,
    private val withTokenVerificationUseCase: WithTokenVerificationUseCase,
    private val withAuthorisedCheckUseCase: WithAuthorisedCheckUseCase,
    private val parser: RuTrackerParser,
) {

    suspend operator fun invoke(
        token: String,
        searchQuery: String?,
        categories: String?,
        author: String?,
        authorId: String?,
        sortType: SearchSortTypeDto?,
        sortOrder: SearchSortOrderDto?,
        period: SearchPeriodDto?,
        page: Int?,
    ): SearchPageDto {
        return withTokenVerificationUseCase(token) { validToken ->
            withAuthorisedCheckUseCase(
                api.search(
                    token = validToken,
                    searchQuery = searchQuery,
                    categories = categories,
                    author = author,
                    authorId = authorId,
                    sortType = sortType,
                    sortOrder = sortOrder,
                    period = period,
                    page = page,
                ),
                parser::parseSearchPage,
            )
        }
    }
}
