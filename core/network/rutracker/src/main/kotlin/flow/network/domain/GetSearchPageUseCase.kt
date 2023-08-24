package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.forum.CategoryDto
import flow.network.dto.search.SearchPageDto
import flow.network.dto.search.SearchPeriodDto
import flow.network.dto.search.SearchSortOrderDto
import flow.network.dto.search.SearchSortTypeDto
import flow.network.dto.topic.AuthorDto
import flow.network.dto.topic.TorrentDto
import flow.network.dto.topic.TorrentStatusDto
import org.jsoup.Jsoup

internal class GetSearchPageUseCase(
    private val api: RuTrackerInnerApi,
    private val withTokenVerificationUseCase: WithTokenVerificationUseCase,
    private val withAuthorisedCheckUseCase: WithAuthorisedCheckUseCase,
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
                ::parseSearchPage,
            )
        }
    }

    companion object {
        fun parseSearchPage(html: String): SearchPageDto {
            val doc = Jsoup.parse(html)
            val navigation =
                doc.select("#main_content_wrap > div.bottom_info > div.nav > p:nth-child(1)")
            val currentPage = navigation.select("b:nth-child(1)").toInt(1)
            val totalPages = navigation.select("b:nth-child(2)").toInt(1)
            val torrents = doc.select(".hl-tr").map { element ->
                val id = element.select(".t-title > a").attr("data-topic_id")
                val status = ParseTorrentStatusUseCase(element) ?: TorrentStatusDto.Checking
                val titleWithTags = element.select(".t-title > a").toStr()
                val title = getTitle(titleWithTags)
                val tags = getTags(titleWithTags)
                val authorId = element.select(".u-name > a").queryParamOrNull("pid")
                val authorName = element.select(".u-name > a").text()
                val author = AuthorDto(id = authorId, name = authorName)
                val categoryId = element.select(".f").queryParam("f")
                val categoryName = element.select(".f").toStr()
                val size = formatSize(element.select(".tor-size").attr("data-ts_text").toLong())
                val date = element.select("[style]").attr("data-ts_text").toLongOrNull()
                val seeds = element.select(".seedmed").toIntOrNull()
                val leeches = element.select(".leechmed").toIntOrNull()
                TorrentDto(
                    id = id,
                    title = title,
                    author = author,
                    category = CategoryDto(categoryId, categoryName),
                    tags = tags,
                    status = status,
                    date = date,
                    size = size,
                    seeds = seeds,
                    leeches = leeches,
                )
            }
            return SearchPageDto(currentPage, totalPages, torrents)
        }
    }
}
