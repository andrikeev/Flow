package flow.data.impl.service

import flow.auth.api.TokenProvider
import flow.data.api.service.SearchService
import flow.data.converters.toDto
import flow.data.converters.toSearchPage
import flow.models.Page
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Torrent
import flow.network.api.NetworkApi
import javax.inject.Inject

class SearchServiceImpl @Inject constructor(
    private val networkApi: NetworkApi,
    private val tokenProvider: TokenProvider,
) : SearchService {

    override suspend fun search(filter: Filter, page: Int): Page<Torrent> {
        return networkApi.getSearchPage(
            token = tokenProvider.getToken(),
            searchQuery = filter.query.orEmpty(),
            sortType = filter.sort.toDto(),
            sortOrder = filter.order.toDto(),
            period = filter.period.toDto(),
            author = filter.author?.name.orEmpty(),
            authorId = filter.author?.id.orEmpty(),
            categories = filter.categories?.map(Category::id)?.joinToString(",").orEmpty(),
            page = page,
        ).toSearchPage()
    }
}
