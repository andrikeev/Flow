package flow.data.impl

import flow.data.api.SearchRepository
import flow.models.Page
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Torrent
import flow.network.NetworkApi
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val networkApi: NetworkApi,
) : SearchRepository {

    override suspend fun search(filter: Filter, page: Int): Page<Torrent> {
        return networkApi.search(
            query = filter.query.orEmpty(),
            sort = filter.sort,
            order = filter.order,
            period = filter.period,
            author = filter.author?.name.orEmpty(),
            authorId = filter.author?.id.orEmpty(),
            categories = filter.categories?.map(Category::id)?.joinToString(",").orEmpty(),
            page = page,
        )
    }
}
