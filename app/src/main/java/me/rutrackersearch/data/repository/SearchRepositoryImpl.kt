package me.rutrackersearch.data.repository

import me.rutrackersearch.domain.repository.SearchRepository
import me.rutrackersearch.models.Page
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.topic.Torrent
import me.rutrackersearch.network.NetworkApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val api: NetworkApi,
) : SearchRepository {

    override suspend fun search(filter: Filter, page: Int): Page<Torrent> {
        return api.search(
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
